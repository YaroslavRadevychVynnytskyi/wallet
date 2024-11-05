package com.nerdysoft.axon.handler.event;

import com.nerdysoft.axon.event.account.AccountCreatedEvent;
import com.nerdysoft.model.enums.Currency;
import com.nerdysoft.axon.command.wallet.CreateWalletCommand;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountEventsHandler {
  private final CommandGateway commandGateway;

  @EventHandler
  public void on(AccountCreatedEvent event) {
    CreateWalletCommand createWalletCommand = new CreateWalletCommand(event.getAccountId(), Currency.USD);
    commandGateway.sendAndWait(createWalletCommand);
  }
}
