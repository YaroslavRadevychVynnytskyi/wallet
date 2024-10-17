package com.nerdysoft.walletservice.axon.aggregate;

import com.nerdysoft.axon.event.WalletCreatedEvent;
import com.nerdysoft.model.enums.Currency;
import com.nerdysoft.walletservice.axon.command.CreateWalletCommand;
import com.nerdysoft.walletservice.dto.request.CreateWalletDto;
import com.nerdysoft.walletservice.model.Wallet;
import com.nerdysoft.walletservice.service.WalletService;
import java.util.UUID;
import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

@Aggregate
@NoArgsConstructor
public class WalletAggregate {
  @AggregateIdentifier
  private UUID walletId;

  private UUID accountId;

  private Currency currency;

  @CommandHandler
  public WalletAggregate(CreateWalletCommand command, WalletService walletService) {
    CreateWalletDto createWalletDto = new CreateWalletDto(command.getAccountId(), command.getCurrency());
    Wallet wallet = walletService.createWallet(createWalletDto);
    walletId = wallet.getWalletId();
    AggregateLifecycle.apply(new WalletCreatedEvent(wallet.getAccountId(), wallet.getWalletId(), wallet.getCurrency()));
  }

  @EventSourcingHandler
  public void on(WalletCreatedEvent event) {
    walletId = event.getWalletId();
    accountId = event.getAccountId();
    currency = event.getCurrency();
  }
}
