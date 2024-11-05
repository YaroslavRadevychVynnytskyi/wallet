package com.nerdysoft.axon.command.wallet;

import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Getter
@RequiredArgsConstructor
public class DeleteWalletCommand {
  @TargetAggregateIdentifier
  private final UUID walletId;
}
