package com.nerdysoft.axon.event.wallet;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CanceledWithdrawFromWalletEvent {
  private UUID walletId;

  private UUID transactionId;

  private BigDecimal balance;
}
