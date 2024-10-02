package com.nerdysoft.dto.feign;

import com.nerdysoft.dto.feign.enums.TransactionStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record Transaction (
        UUID transactionId,
        UUID fromWalletId,
        UUID toWalletId,
        BigDecimal amount,
        BigDecimal walletBalance,
        String currency,
        TransactionStatus status,
        LocalDateTime createdAt
) {
}