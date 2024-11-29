package com.nerdysoft.dto.response;

import com.nerdysoft.model.enums.Currency;
import com.nerdysoft.model.enums.TransactionStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
public abstract class WalletOperationResponseDto {
  private UUID transactionId;

  private UUID accountId;

  private BigDecimal amount;

  private BigDecimal walletBalance;

  private Currency operationCurrency;

  private Currency walletCurrency;

  private TransactionStatus status;

  private LocalDateTime createdAt;
}
