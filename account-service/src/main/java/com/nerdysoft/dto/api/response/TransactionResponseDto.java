package com.nerdysoft.dto.api.response;

import com.nerdysoft.dto.feign.Currency;
import com.nerdysoft.dto.feign.Transaction;
import com.nerdysoft.dto.feign.TransactionStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionResponseDto(
        UUID transactionId,
        UUID fromAccountId,
        UUID toAccountId,
        BigDecimal amount,
        BigDecimal commissionFee,
        Currency currency,
        TransactionStatus status,
        LocalDateTime createdAt
) {
    public TransactionResponseDto(Transaction transaction, BigDecimal commissionFee, UUID fromAccountId, UUID toAccountId) {
        this(
                transaction.transactionId(),
                fromAccountId,
                toAccountId,
                transaction.amount(),
                commissionFee,
                transaction.currency(),
                transaction.status(),
                transaction.createdAt()
        );
    }
}
