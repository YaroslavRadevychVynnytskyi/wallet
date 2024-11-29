package com.nerdysoft.axon.event.bankreserve;

import com.nerdysoft.model.enums.Currency;
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
public class CanceledReceiveCommissionEvent {
  private UUID commissionId;

  private UUID transactionId;

  private UUID loanLimitId;

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
