package com.nerdysoft.axon.command.wallet;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CancelWithdrawFromWalletCommand {
  @TargetAggregateIdentifier
  private UUID walletId;

  private UUID transactionId;

  private BigDecimal amount;

  private BigDecimal usedLoanLimitAvailableAmount;
}
