package com.nerdysoft.walletservice.dto.response;

import com.nerdysoft.walletservice.model.enums.Currency;
import com.nerdysoft.walletservice.model.enums.TransactionStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionResponseDto(UUID transactionId,
                                     UUID walletId,
                                     BigDecimal amount,
                                     Currency currency,
                                     TransactionStatus status,
                                     LocalDateTime createdAt) {}
