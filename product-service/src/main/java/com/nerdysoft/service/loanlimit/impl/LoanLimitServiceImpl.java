package com.nerdysoft.service.loanlimit.impl;

import com.nerdysoft.dto.api.request.LoanLimitRequestDto;
import com.nerdysoft.dto.feign.ConvertAmountRequestDto;
import com.nerdysoft.dto.feign.Wallet;
import com.nerdysoft.dto.feign.enums.Currency;
import com.nerdysoft.entity.loanlimit.LoanLimit;
import com.nerdysoft.feign.CurrencyExchangeFeignClient;
import com.nerdysoft.feign.WalletFeignClient;
import com.nerdysoft.repo.loanlimit.LoanLimitRepository;
import com.nerdysoft.service.analyzer.WalletBalanceAnalyzer;
import com.nerdysoft.service.loanlimit.LoanLimitService;
import com.nerdysoft.service.loanlimit.strategy.handlers.LoanLimitHandler;
import com.nerdysoft.service.loanlimit.strategy.LoanLimitStrategy;
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

    private final WalletFeignClient walletFeignClient;
    private final CurrencyExchangeFeignClient currencyExchangeFeignClient;

    @Transactional
    @Override
    public LoanLimit getLoanLimit(LoanLimitRequestDto requestDto) {
        hasExistingLoanLimit(requestDto.accountId());

        Wallet wallet = walletFeignClient.getWalletByAccountIdAndCurrency(requestDto.accountId(), requestDto.currency()).getBody();

        BigDecimal maxBalanceForLastMonth = walletBalanceAnalyzer.getMaxBalanceForLastMonth(wallet.walletId());
        BigDecimal turnoverForLastMonth = walletBalanceAnalyzer.getTurnoverForLastMonth(wallet.walletId());

        LoanLimitHandler loanLimitHandler = loanLimitStrategy.get(
                convertToUsd(requestDto.currency(), maxBalanceForLastMonth),
                convertToUsd(requestDto.currency(), turnoverForLastMonth)
        );

        LoanLimit loanLimit = new LoanLimit(
                requestDto.accountId(),
                wallet.walletId(),
                wallet.currency(),
                convert(Currency.USD, wallet.currency(), loanLimitHandler.getLoanLimit())
        );

        return loanLimitRepository.save(loanLimit);
    }

    @Override
    public LoanLimit getLoanLimitByWalletId(UUID walletId) {
        return loanLimitRepository.findByWalletId(walletId)
                .orElseThrow(() -> new EntityNotFoundException("Can't find loan limit with wallet ID: " + walletId));
    }

    @Override
    public LoanLimit updateByWalletId(UUID walletId, LoanLimit loanLimit) {
        LoanLimit oldLoanLimit = getLoanLimitByWalletId(walletId);
        oldLoanLimit.setAvailableLoanLimit(loanLimit.getAvailableLoanLimit());

        return loanLimitRepository.save(oldLoanLimit);
    }

    private void hasExistingLoanLimit(UUID accountId) {
        if (loanLimitRepository.existsByAccountId(accountId)) {
            throw new IllegalStateException("Loan limit already assigned to account with ID: " + accountId);
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
}
