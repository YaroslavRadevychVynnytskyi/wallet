package com.nerdysoft.axon.command.wallet;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Getter
@RequiredArgsConstructor
public class UpdateBalanceForReceiverWalletCommand {
  @TargetAggregateIdentifier
  private final UUID walletId;

  private final BigDecimal balance;
}
