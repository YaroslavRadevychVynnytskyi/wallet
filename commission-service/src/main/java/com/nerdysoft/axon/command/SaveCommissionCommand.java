package com.nerdysoft.axon.command;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SaveCommissionCommand {
  private final UUID transactionId;
  private final BigDecimal usdCommission;
  private final BigDecimal originalCurrencyCommission;
  private final BigDecimal walletAmount;
  private final boolean isLoanLimitUsed;
  private final BigDecimal loanLimitAmount;
  private final String fromWalletCurrency;
  private final String toWalletCurrency;
  private final String transactionCurrency;
}
