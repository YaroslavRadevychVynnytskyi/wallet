package com.nerdysoft.dto.feign;

import com.nerdysoft.dto.feign.enums.TransactionStatus;
import com.nerdysoft.model.enums.Currency;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record Transaction (
        UUID transactionId,
        UUID walletId,
        UUID toWalletId,
        BigDecimal walletBalance,
        BigDecimal amount,
        Currency currency,
        TransactionStatus status,
        LocalDateTime createdAt
) {
}