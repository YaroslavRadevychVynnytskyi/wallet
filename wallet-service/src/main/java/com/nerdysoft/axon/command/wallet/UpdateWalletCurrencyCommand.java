package com.nerdysoft.axon.command.wallet;

import com.nerdysoft.model.enums.Currency;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Getter
@RequiredArgsConstructor
public class UpdateWalletCurrencyCommand {
  @TargetAggregateIdentifier
  private final UUID walletId;

  private final Currency currency;
}
