package com.nerdysoft.axon.command.loanlimit;

import com.nerdysoft.model.enums.Currency;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CancelUpdateLoanLimitAfterTransferCommand {
  @TargetAggregateIdentifier
  private UUID loanLimitId;

  private UUID transactionId;

  private UUID accountId;

  private UUID fromWalletId;

  private UUID toWalletId;

  private BigDecimal cleanAmount;

  private Currency operationCurrency;

  private Currency walletCurrency;

  private boolean usedLoanLimit;

  private BigDecimal usedLoanLimitAmount;

  private BigDecimal commission;
}
