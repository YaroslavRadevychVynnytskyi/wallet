package com.nerdysoft.axon.event.wallet;

import com.nerdysoft.model.enums.Currency;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class WalletCreatedEvent {
  private UUID walletId;

  private Currency currency;
}
