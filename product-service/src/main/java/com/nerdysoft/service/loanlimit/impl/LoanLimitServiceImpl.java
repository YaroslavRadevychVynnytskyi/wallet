package com.nerdysoft.service.loanlimit.impl;

import com.nerdysoft.dto.feign.BankReserveTypeDto;
import com.nerdysoft.dto.feign.ConvertAmountRequestDto;
import com.nerdysoft.dto.feign.TransactionRequestDto;
import com.nerdysoft.dto.feign.UpdateBalanceDto;
import com.nerdysoft.dto.feign.Wallet;
import com.nerdysoft.entity.loanlimit.LoanLimit;
import com.nerdysoft.feign.BankReserveFeignClient;
import com.nerdysoft.feign.CurrencyExchangeFeignClient;
import com.nerdysoft.feign.WalletFeignClient;
import com.nerdysoft.model.enums.Currency;
import com.nerdysoft.model.enums.OperationType;
import com.nerdysoft.model.enums.ReserveType;
import com.nerdysoft.repo.loanlimit.LoanLimitRepository;
import com.nerdysoft.service.analyzer.WalletBalanceAnalyzer;
import com.nerdysoft.service.loanlimit.LoanLimitService;
import com.nerdysoft.service.loanlimit.strategy.LoanLimitStrategy;
import com.nerdysoft.service.loanlimit.strategy.handlers.LoanLimitHandler;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LoanLimitServiceImpl implements LoanLimitService {
    private final LoanLimitRepository loanLimitRepository;
    private final WalletBalanceAnalyzer walletBalanceAnalyzer;
    private final LoanLimitStrategy loanLimitStrategy;
    private final BankReserveFeignClient bankReserveFeignClient;
    private final WalletFeignClient walletFeignClient;
    private final CurrencyExchangeFeignClient currencyExchangeFeignClient;

    @Transactional
    @Override
    public LoanLimit getLoanLimit(UUID accountId, String email, Currency currency) {
        hasExistingLoanLimit(accountId);

        Wallet wallet = walletFeignClient.getWalletByAccountIdAndCurrency(accountId, currency).getBody();

        BigDecimal maxBalanceForLastMonth = walletBalanceAnalyzer.getMaxBalanceForLastMonth(wallet.walletId());
        BigDecimal turnoverForLastMonth = walletBalanceAnalyzer.getTurnoverForLastMonth(wallet.walletId(), wallet.currency());

        LoanLimitHandler loanLimitHandler = loanLimitStrategy.get(
                convertToUsd(currency, maxBalanceForLastMonth),
                convertToUsd(currency, turnoverForLastMonth)
        );

        LoanLimit loanLimit = new LoanLimit(
                accountId,
                email,
                wallet.walletId(),
                wallet.currency(),
                convert(Currency.USD, wallet.currency(), loanLimitHandler.getLoanLimit())
        );

        UUID bankReserveId = bankReserveFeignClient.getReserveIdByType(new BankReserveTypeDto(
            ReserveType.LOAN_LIMIT)).getBody();
        bankReserveFeignClient.updateBalance(new UpdateBalanceDto(bankReserveId, ReserveType.LOAN_LIMIT, loanLimitHandler.getLoanLimit(), OperationType.WITHDRAW));

        return loanLimitRepository.save(loanLimit);
    }

    @Override
    public LoanLimit getLoanLimitByWalletId(UUID walletId) {
        return loanLimitRepository.findByWalletIdAndIsRepaidFalse(walletId)
                .orElseThrow(() -> new EntityNotFoundException("Can't find loan limit with wallet ID: " + walletId));
    }

    @Override
    public LoanLimit updateByWalletId(UUID walletId, LoanLimit loanLimit) {
        LoanLimit oldLoanLimit = getLoanLimitByWalletId(walletId);
        oldLoanLimit.setAvailableAmount(loanLimit.getAvailableAmount());

        return loanLimitRepository.save(oldLoanLimit);
    }

    @Transactional
    @Override
    public LoanLimit repayLoanLimit(UUID accountId) {
        LoanLimit loanLimit = getLoanLimitByAccountId(accountId);

        walletFeignClient.withdraw(loanLimit.getWalletId(), new TransactionRequestDto(loanLimit.getInitialAmount(), loanLimit.getCurrency()));

        UUID bankReserveId = bankReserveFeignClient.getReserveIdByType(new BankReserveTypeDto(ReserveType.LOAN_LIMIT)).getBody();
        bankReserveFeignClient.updateBalance(new UpdateBalanceDto(bankReserveId, ReserveType.LOAN_LIMIT, loanLimit.getInitialAmount(), OperationType.DEPOSIT));

        loanLimit.setAvailableAmount(BigDecimal.ZERO);
        loanLimit.setRepaid(true);

        return loanLimitRepository.save(loanLimit);
    }

    private void hasExistingLoanLimit(UUID accountId) {
        if (loanLimitRepository.existsByAccountIdAndIsRepaidFalse(accountId)) {
            throw new IllegalStateException("Account with ID: " + accountId + " already has an active loan limit");
        }
    }

    private BigDecimal convertToUsd(Currency fromCurrency, BigDecimal amount) {
        return (fromCurrency.equals(Currency.USD)) ? amount : currencyExchangeFeignClient
                .convert(new ConvertAmountRequestDto(fromCurrency.getCode(), Currency.USD.getCode(), amount))
                .getBody()
                .convertedAmount();
    }

    private BigDecimal convert(Currency fromCurrency, Currency toCurrency, BigDecimal amount) {
        return currencyExchangeFeignClient
                .convert(new ConvertAmountRequestDto(fromCurrency.getCode(), toCurrency.getCode(), amount))
                .getBody()
                .convertedAmount();
    }

    private LoanLimit getLoanLimitByAccountId(UUID accountId) {
        return loanLimitRepository.findByAccountIdAndIsRepaidFalse(accountId).orElseThrow(() ->
                new EntityNotFoundException("Can't find not repaid loan limit with account ID: " + accountId));
    }
}
