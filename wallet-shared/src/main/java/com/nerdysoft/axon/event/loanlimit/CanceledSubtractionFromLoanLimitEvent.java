package com.nerdysoft.axon.event.loanlimit;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CanceledSubtractionFromLoanLimitEvent {
  private UUID loanLimitId;

  private UUID transactionId;

  private UUID walletId;

  private BigDecimal amount;

  private BigDecimal usedLoanLimitAmount;
}
