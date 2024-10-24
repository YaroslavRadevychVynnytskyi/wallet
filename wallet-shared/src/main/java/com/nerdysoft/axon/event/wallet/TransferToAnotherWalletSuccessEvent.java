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
public class TransferToAnotherWalletSuccessEvent {
  private UUID walletId;

  private BigDecimal balance;

  private BigDecimal amount;

  private Currency currency;

  private UUID transactionId;
}
