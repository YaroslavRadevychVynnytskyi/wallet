package com.nerdysoft.walletservice.dto.response;

import com.nerdysoft.model.enums.Currency;
import com.nerdysoft.model.enums.TransactionStatus;
import com.nerdysoft.walletservice.dto.request.WalletOperationResponseDto;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionResponseDto(UUID transactionId,
                                     UUID walletId,
                                     BigDecimal walletBalance,
                                     BigDecimal amount,
                                     Currency currency,
                                     TransactionStatus status,
                                     LocalDateTime createdAt) implements
    WalletOperationResponseDto {
}
