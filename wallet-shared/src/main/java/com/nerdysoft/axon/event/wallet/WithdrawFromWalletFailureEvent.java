package com.nerdysoft.axon.event.wallet;

import com.nerdysoft.model.enums.Currency;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawFromWalletFailureEvent {
  private UUID transactionId;

  private UUID walletId;

  private BigDecimal amount;

  private BigDecimal balance;

  private boolean usedLoanLimit;

  private BigDecimal usedLoanLimitAmount;

  private Currency operationCurrency;
}
