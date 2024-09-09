package com.nerdysoft.dto.feign;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record Transaction(
        UUID transactionId,
        UUID walletId,
        UUID toWalletId,
        BigDecimal amount,
        Currency currency,
        TransactionStatus status,
        LocalDate createdAt
) {
}
