package com.nerdysoft.axon.command.loanlimit;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateLoanLimitAfterWithdrawCommand {
  @TargetAggregateIdentifier
  private UUID loanLimitId;

  private UUID transactionId;

  private BigDecimal usedAvailableAmount;

  private UUID walletId;

  private BigDecimal amount;
}
