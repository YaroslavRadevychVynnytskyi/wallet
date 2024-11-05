package com.nerdysoft.axon.aggregate;

import com.nerdysoft.axon.command.account.CreateAccountCommand;
import com.nerdysoft.axon.command.account.DeleteAccountCommand;
import com.nerdysoft.axon.command.account.UpdateAccountCommand;
import com.nerdysoft.axon.event.account.AccountCreatedEvent;
import com.nerdysoft.axon.event.account.AccountDeletedEvent;
import com.nerdysoft.axon.event.account.AccountUpdatedEvent;
import com.nerdysoft.dto.api.request.UpdateAccountRequestDto;
import com.nerdysoft.entity.Account;
import com.nerdysoft.entity.Role;
import com.nerdysoft.service.AccountService;
import java.util.List;
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

  private String fullName;

  private String email;

  private String password;

  private List<Role> roles;

  @CommandHandler
  public AccountAggregate(CreateAccountCommand command, AccountService accountService) {
    Account account = Account.builder()
        .email(command.getEmail())
        .fullName(command.getFullName())
        .password(command.getPassword())
        .build();
    account = accountService.create(account);
    accountId = account.getAccountId();
    fullName = account.getFullName();
    email = account.getEmail();
    password = account.getPassword();
    roles = account.getRoles();
    AggregateLifecycle.apply(new AccountCreatedEvent(account.getAccountId()));
  }

  @CommandHandler
  public Account handle(UpdateAccountCommand command, AccountService accountService) {
    Account account = accountService.update(command.getAccountId(), new UpdateAccountRequestDto(command.getFullName(),
        command.getEmail()));
    AggregateLifecycle.apply(new AccountUpdatedEvent(account.getAccountId(), account.getFullName(), account.getEmail()));
    return account;
  }

  @EventSourcingHandler
  public void on(AccountUpdatedEvent event) {
    fullName = event.getFullName();
    email = event.getEmail();
  }

  @CommandHandler
  public void handle(DeleteAccountCommand command, AccountService accountService) {
    accountService.deleteById(command.getAccountId());
    AggregateLifecycle.apply(new AccountDeletedEvent(command.getAccountId()));
  }

  @EventSourcingHandler
  public void on(AccountDeletedEvent event) {
    AggregateLifecycle.markDeleted();
  }
}
