package com.nerdysoft.dto.response;

import com.nerdysoft.dto.request.TransferTransactionResponseDto;
import com.nerdysoft.entity.enums.Currency;
import com.nerdysoft.entity.enums.TransactionStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionResponseDto(UUID transactionId,
                                     UUID walletId,
                                     BigDecimal amount,
                                     Currency currency,
                                     TransactionStatus status,
                                     LocalDateTime createdAt) implements TransferTransactionResponseDto {
}
