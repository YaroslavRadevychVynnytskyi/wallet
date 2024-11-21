package com.nerdysoft.service.loanlimit.impl;

import com.nerdysoft.axon.command.loanlimit.CancelSubtractionFromLoanLimitCommand;
import com.nerdysoft.axon.query.wallet.FindWalletByAccountIdAndCurrencyQuery;
import com.nerdysoft.dto.feign.BankReserveTypeDto;
import com.nerdysoft.dto.feign.ConvertAmountRequestDto;
import com.nerdysoft.dto.feign.TransactionRequestDto;
import com.nerdysoft.dto.feign.UpdateBalanceDto;
import com.nerdysoft.dto.wallet.WalletDto;
import com.nerdysoft.entity.loanlimit.LoanLimit;
import com.nerdysoft.feign.BankReserveFeignClient;
import com.nerdysoft.feign.CurrencyExchangeFeignClient;
import com.nerdysoft.feign.WalletFeignClient;
import com.nerdysoft.model.enums.Currency;
import com.nerdysoft.model.enums.OperationType;
import com.nerdysoft.model.enums.ReserveType;
import com.nerdysoft.model.exception.UniqueException;
import com.nerdysoft.repo.loanlimit.LoanLimitRepository;
import com.nerdysoft.service.analyzer.WalletBalanceAnalyzer;
import com.nerdysoft.service.loanlimit.LoanLimitService;
import com.nerdysoft.service.loanlimit.strategy.LoanLimitStrategy;
import com.nerdysoft.service.loanlimit.strategy.handlers.LoanLimitHandler;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.http.HttpStatus;
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
    private final QueryGateway queryGateway;

    @Override
    public LoanLimit findById(UUID id) {
        return loanLimitRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Loan limit not found"));
    }

    @Transactional
    @Override
    public LoanLimit getLoanLimit(UUID accountId, String email, Currency currency) {
        if (!loanLimitRepository.checkIfExistsNotRepaidLoanLimitByAccountId(accountId)) {
            WalletDto wallet = queryGateway.query(new FindWalletByAccountIdAndCurrencyQuery(accountId, currency),
                WalletDto.class).join();

            BigDecimal maxBalanceForLastMonth = walletBalanceAnalyzer.getMaxBalanceForLastMonth(wallet.getWalletId());
            BigDecimal turnoverForLastMonth = walletBalanceAnalyzer.getTurnoverForLastMonth(wallet.getWalletId(), wallet.getCurrency());

            LoanLimitHandler loanLimitHandler = loanLimitStrategy.get(
                convertToUsd(currency, maxBalanceForLastMonth),
                convertToUsd(currency, turnoverForLastMonth)
            );

            LoanLimit loanLimit = new LoanLimit(
                accountId,
                email,
                wallet.getWalletId(),
                wallet.getCurrency(),
                convert(Currency.USD, wallet.getCurrency(), loanLimitHandler.getLoanLimit())
            );

            UUID bankReserveId = bankReserveFeignClient.getReserveIdByType(new BankReserveTypeDto(
                ReserveType.LOAN_LIMIT)).getBody();
            bankReserveFeignClient.updateBalance(new UpdateBalanceDto(bankReserveId, ReserveType.LOAN_LIMIT, loanLimitHandler.getLoanLimit(), OperationType.WITHDRAW));

            return loanLimitRepository.save(loanLimit);
        } else {
            throw new UniqueException("Account has already a loan limit", HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @Override
    public LoanLimit getLoanLimitByWalletId(UUID walletId) {
        return loanLimitRepository.findNotRepaidLoanLimitByWalletId(walletId)
                .orElseThrow(() -> new EntityNotFoundException("Can't find loan limit with wallet ID: " + walletId));
    }

    @Override
    public LoanLimit subtractAvailableLoanLimitAmount(UUID loanLimitId, BigDecimal usedLoanLimitAmount) {
        LoanLimit loanLimit = findById(loanLimitId);

        loanLimit.setAvailableAmount(loanLimit.getAvailableAmount().subtract(usedLoanLimitAmount));

        return loanLimitRepository.save(loanLimit);
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

    @Override
    public LoanLimit cancelSubtractionFromLoanLimit(CancelSubtractionFromLoanLimitCommand command) {
        LoanLimit loanLimit = findById(command.getLoanLimitId());

        loanLimit.setAvailableAmount(loanLimit.getAvailableAmount().add(command.getUsedAvailableAmount()));

        return loanLimitRepository.save(loanLimit);
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
        return loanLimitRepository.findNotRepaidLoanLimitByAccountId(accountId).orElseThrow(() ->
                new EntityNotFoundException("Can't find not repaid loan limit with account ID: " + accountId));
    }
}
