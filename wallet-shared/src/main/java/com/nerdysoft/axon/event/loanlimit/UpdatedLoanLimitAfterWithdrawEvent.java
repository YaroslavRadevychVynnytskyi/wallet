package com.nerdysoft.axon.event.loanlimit;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdatedLoanLimitAfterWithdrawEvent {
  private UUID transactionId;

  private UUID loanLimitId;

  private BigDecimal usedAvailableAmount;

  private BigDecimal availableAmount;

  private UUID walletId;

  private BigDecimal amount;
}
