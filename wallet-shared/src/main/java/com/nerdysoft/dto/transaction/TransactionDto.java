package com.nerdysoft.dto.transaction;

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
public class TransactionDto {
  private UUID transactionId;

  private UUID accountId;

  private UUID walletId;

  private UUID toWalletId;

  private BigDecimal walletBalance;

  private BigDecimal amount;

  private boolean usedLoanLimit;

  private BigDecimal usedLoanLimitAmount;

  private BigDecimal commission;

  private Currency operationCurrency;

  private Currency walletCurrency;

  private TransactionStatus status;

  private LocalDateTime createdAt;
}
