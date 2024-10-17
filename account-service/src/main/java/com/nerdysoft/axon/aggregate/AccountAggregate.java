package com.nerdysoft.axon.aggregate;

import com.nerdysoft.axon.command.CreateAccountCommand;
import com.nerdysoft.axon.event.AccountCreatedEvent;
import com.nerdysoft.dto.api.request.CreateAccountRequestDto;
import com.nerdysoft.entity.Account;
import com.nerdysoft.service.AccountService;
import java.util.UUID;
import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

@Aggregate
@NoArgsConstructor
public class AccountAggregate {
  @AggregateIdentifier
  private UUID accountId;

  private String email;

  @CommandHandler
  public AccountAggregate(CreateAccountCommand command, AccountService accountService) {
    CreateAccountRequestDto createAccountRequestDto = new CreateAccountRequestDto(
        command.getFullName(), command.getEmail(), command.getPassword());
    Account account = accountService.create(createAccountRequestDto);
    accountId = account.getAccountId();
    AggregateLifecycle.apply(new AccountCreatedEvent(account.getAccountId(), account.getEmail()));
  }

  @EventSourcingHandler
  public void on(AccountCreatedEvent event) {
    accountId = event.getAccountId();
    email = event.getEmail();
  }
}
