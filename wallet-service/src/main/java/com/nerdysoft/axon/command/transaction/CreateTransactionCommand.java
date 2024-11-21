package com.nerdysoft.axon.command.transaction;

import com.nerdysoft.model.enums.Currency;
import com.nerdysoft.model.enums.TransactionStatus;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTransactionCommand {
  private UUID walletId;

  private BigDecimal walletBalance;

  private BigDecimal amount;

  private boolean usedLoanLimit;

  private BigDecimal usedLoanLimitAmount;

  private UUID toWalletId;

  private Currency operationCurrency;

  private Currency walletCurrency;

  private TransactionStatus status;
}
