package com.nerdysoft.axon.query.commission;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CalculateCommissionQuery {
  private BigDecimal usedWalletOwnAmount;
  private boolean loanLimitUsed;
  private BigDecimal usedLoanLimitAmount;
  private String fromWalletCurrency;
  private String toWalletCurrency;
  private String transactionCurrency;
}
