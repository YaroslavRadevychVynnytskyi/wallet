package com.nerdysoft.service.analyzer.impl;

import com.nerdysoft.axon.query.transaction.FindAllAccountTransactionByLastMonthQuery;
import com.nerdysoft.dto.feign.ConvertAmountRequestDto;
import com.nerdysoft.dto.feign.Transaction;
import com.nerdysoft.dto.transaction.TransactionDto;
import com.nerdysoft.feign.CurrencyExchangeFeignClient;
import com.nerdysoft.feign.WalletFeignClient;
import com.nerdysoft.model.enums.Currency;
import com.nerdysoft.model.enums.TransactionStatus;
import com.nerdysoft.service.analyzer.WalletBalanceAnalyzer;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WalletBalanceAnalyzerImpl implements WalletBalanceAnalyzer {
    private final WalletFeignClient walletFeignClient;
    private final CurrencyExchangeFeignClient currencyExchangeFeignClient;

    private final QueryGateway queryGateway;

    @Override
    public BigDecimal getMaxBalanceForLastMonth(UUID walletId) {
        return walletFeignClient.getTransactionsByWalletId(walletId).getBody().stream()
                .filter(this::isTransactionWithinLastMonth)
                .map(Transaction::walletBalance)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
    }

    @Override
    public BigDecimal getTurnoverForLastMonth(UUID walletId, Currency walletCurrency) {
        return walletFeignClient.getTransactionsByWalletId(walletId).getBody().stream()
                .filter(this::isTransactionWithinLastMonth)
                .map(t -> convertToWalletCurrency(t, walletCurrency).amount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public BigDecimal getMaxBalanceForLastMonthByAccount(UUID accountId) {
        return queryGateway.query(new FindAllAccountTransactionByLastMonthQuery(accountId),
                ResponseTypes.multipleInstancesOf(TransactionDto.class)).join().stream()
            .map(TransactionDto::getWalletBalance)
            .max(BigDecimal::compareTo)
            .orElse(BigDecimal.ZERO);
    }

    @Override
    public BigDecimal getTurnoverForLastMonthByAccount(UUID accountId) {
        return queryGateway.query(new FindAllAccountTransactionByLastMonthQuery(accountId),
                ResponseTypes.multipleInstancesOf(TransactionDto.class)).join().stream()
            .map(this::convertToUsd)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private boolean isTransactionWithinLastMonth(Transaction transaction) {
        return transaction.status().equals(TransactionStatus.SUCCESS)
                && transaction.createdAt().isAfter(LocalDateTime.now().minusMonths(1));
    }

    private Transaction convertToWalletCurrency(Transaction transaction, Currency walletCurrency) {
        if (transaction.currency().equals(walletCurrency)) {
            return transaction;
        }

        transaction.setAmount(currencyExchangeFeignClient.convert(new ConvertAmountRequestDto(
                transaction.currency().getCode(),
                walletCurrency.getCode(),
                transaction.amount()
        )).getBody().convertedAmount());

        return transaction;
    }

    private BigDecimal convertToUsd(TransactionDto transaction) {
        if (transaction.getWalletCurrency().equals(Currency.USD)) {
            return transaction.getAmount();
        } else {
            return currencyExchangeFeignClient.convert(new ConvertAmountRequestDto(
                transaction.getWalletCurrency().getCode(),
                Currency.USD.getCode(),
                transaction.getAmount()
            )).getBody().convertedAmount();
        }
    }
}
