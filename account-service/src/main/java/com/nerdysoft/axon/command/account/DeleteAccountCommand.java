package com.nerdysoft.axon.command.account;

import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Getter
@RequiredArgsConstructor
public class DeleteAccountCommand {
  @TargetAggregateIdentifier
  private final UUID accountId;
}
