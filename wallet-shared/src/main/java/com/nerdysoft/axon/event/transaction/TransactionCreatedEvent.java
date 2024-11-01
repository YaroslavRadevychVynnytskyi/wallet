package com.nerdysoft.axon.event.transaction;

import com.nerdysoft.model.enums.Currency;
import com.nerdysoft.model.enums.TransactionStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionCreatedEvent {
  private UUID transactionId;

  private UUID walletId;

  private UUID toWalletId;

  private BigDecimal walletBalance;

  private BigDecimal amount;

  private Currency currency;

  private TransactionStatus status;

  private LocalDateTime createdAt;
}
