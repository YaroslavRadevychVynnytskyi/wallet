package com.nerdysoft.axon.event.commission;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommissionSavedEvent {
  private UUID commissionId;

  private UUID transactionId;

  private BigDecimal walletAmount;

  private boolean isLoanLimitUsed;

  private BigDecimal loanLimitAmount;

  private BigDecimal usdCommissionAmount;

  private BigDecimal senderCurrencyCommissionAmount;

  private String fromWalletCurrency;

  private String toWalletCurrency;

  private String transactionCurrency;
}
