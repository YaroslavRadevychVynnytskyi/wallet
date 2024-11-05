package com.nerdysoft.axon.query;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CalculateCommissionQuery {
  private BigDecimal walletAmount;
  private boolean isLoanLimitUsed;
  private BigDecimal loanLimitAmount;
  private String fromWalletCurrency;
  private String toWalletCurrency;
  private String transactionCurrency;
}
