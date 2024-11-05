package com.nerdysoft.axon.command.wallet;

import com.nerdysoft.model.enums.Currency;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Getter
@RequiredArgsConstructor
public class CreateWalletCommand {
  private final UUID accountId;

  private final Currency currency;
}
