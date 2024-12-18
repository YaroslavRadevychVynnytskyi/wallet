package com.nerdysoft.dto.feign;

import com.nerdysoft.model.enums.Currency;
import com.nerdysoft.model.enums.TransactionStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record Transaction(
        UUID transactionId,
        UUID fromWalletId,
        UUID toWalletId,
        BigDecimal amount,
        Currency currency,
        TransactionStatus status,
        LocalDateTime createdAt
) {
}
