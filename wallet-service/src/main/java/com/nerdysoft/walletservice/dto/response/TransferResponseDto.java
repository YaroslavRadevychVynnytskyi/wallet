package com.nerdysoft.walletservice.dto.response;

import com.nerdysoft.model.enums.Currency;
import com.nerdysoft.walletservice.dto.request.TransferTransactionResponseDto;
import com.nerdysoft.walletservice.model.enums.TransactionStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransferResponseDto(UUID transactionId,
                                  UUID fromWalletId,
                                  UUID toWalletId,
                                  BigDecimal amount,
                                  BigDecimal walletBalance,
                                  Currency currency,
                                  TransactionStatus status,
                                  LocalDateTime createdAt) implements TransferTransactionResponseDto {
}
