package com.nerdysoft.dto.api.response;

import com.nerdysoft.dto.feign.Currency;
import com.nerdysoft.dto.feign.TransactionStatus;
import com.nerdysoft.dto.feign.WalletTransactionResponseDto;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record GenericTransactionResponseDto(
        UUID transactionId,
        UUID accountId,
        BigDecimal amount,
        Currency currency,
        TransactionStatus status,
        LocalDateTime createdAt
) {
    public GenericTransactionResponseDto(WalletTransactionResponseDto responseDto, UUID accountId) {
        this(
                responseDto.transactionId(),
                accountId,
                responseDto.amount(),
                responseDto.currency(),
                responseDto.status(),
                responseDto.createdAt()
        );
    }
}
