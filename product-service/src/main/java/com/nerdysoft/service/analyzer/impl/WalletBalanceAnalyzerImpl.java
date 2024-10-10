package com.nerdysoft.service.analyzer.impl;

import com.nerdysoft.dto.feign.Transaction;
import com.nerdysoft.dto.feign.enums.TransactionStatus;
import com.nerdysoft.feign.WalletFeignClient;
import com.nerdysoft.service.analyzer.WalletBalanceAnalyzer;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WalletBalanceAnalyzerImpl implements WalletBalanceAnalyzer {
    private final WalletFeignClient walletFeignClient;

    @Override
    public BigDecimal getMaxBalanceForLastMonth(UUID walletId) {
        return walletFeignClient.getTransactionsByWalletId(walletId).getBody().stream()
                .filter(this::isTransactionWithinLastMonth)
                .map(Transaction::walletBalance)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
    }

    @Override
    public BigDecimal getTurnoverForLastMonth(UUID walletId) {
        return walletFeignClient.getTransactionsByWalletId(walletId).getBody().stream()
                .filter(this::isTransactionWithinLastMonth)
                .map(Transaction::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private boolean isTransactionWithinLastMonth(Transaction transaction) {
        return transaction.status().equals(TransactionStatus.SUCCESS)
                && transaction.createdAt().isAfter(LocalDateTime.now().minusMonths(1));
    }
}
