package com.nerdysoft.axon.aggregate;

import com.nerdysoft.axon.command.CreateBalanceCommand;
import com.nerdysoft.axon.command.bankreserve.ReceiveCommissionCommand;
import com.nerdysoft.axon.command.bankreserve.UpdateBalanceCommand;
import com.nerdysoft.axon.command.bankreserve.UpdateBankReserveCommand;
import com.nerdysoft.axon.event.bankreserve.BalanceCreatedEvent;
import com.nerdysoft.axon.event.bankreserve.BalanceUpdatedEvent;
import com.nerdysoft.axon.event.bankreserve.BankReserveUpdatedEvent;
import com.nerdysoft.axon.event.bankreserve.ReceivedCommissionEvent;
import com.nerdysoft.dto.api.response.UpdateBalanceResponseDto;
import com.nerdysoft.model.enums.OperationType;
import com.nerdysoft.model.enums.ReserveType;
import com.nerdysoft.model.reserve.BankReserve;
import com.nerdysoft.service.BankReserveService;
import java.math.BigDecimal;
import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;

@Aggregate
@NoArgsConstructor
public class BankReserveAggregate {
  @AggregateIdentifier
  private ReserveType reserveType;

  @CommandHandler
  public BankReserveAggregate(CreateBalanceCommand createBalanceCommand,
      BankReserveService bankReserveService) {
    BankReserve bankReserve = bankReserveService.create(createBalanceCommand);

    BalanceCreatedEvent createBalanceEvent = new BalanceCreatedEvent();
    BeanUtils.copyProperties(bankReserve, createBalanceEvent);

    AggregateLifecycle.apply(createBalanceEvent);
  }

  @EventSourcingHandler
  public void on(BalanceCreatedEvent balanceCreatedEvent) {
    reserveType = balanceCreatedEvent.getType();
  }

  @CommandHandler
  public UpdateBalanceResponseDto handle(UpdateBalanceCommand updateBalanceCommand,
      BankReserveService bankReserveService) {
    UpdateBalanceResponseDto updateBalanceResponseDto;

    if (updateBalanceCommand.getOperationType().equals(OperationType.DEPOSIT)) {
      updateBalanceResponseDto = bankReserveService.updateBalance(updateBalanceCommand,
          BigDecimal::add);
    } else {
      updateBalanceResponseDto = bankReserveService.updateBalance(updateBalanceCommand,
          BigDecimal::subtract);
    }

    BalanceUpdatedEvent updateBalanceEvent = new BalanceUpdatedEvent();
    BeanUtils.copyProperties(updateBalanceResponseDto, updateBalanceEvent);

    AggregateLifecycle.apply(updateBalanceEvent);
    return updateBalanceResponseDto;
  }

  @CommandHandler
  public void handle(ReceiveCommissionCommand command, BankReserveService bankReserveService) {
    bankReserveService.receiveCommission(command.getReserveType(), command.getCommission());

    ReceivedCommissionEvent event = ReceivedCommissionEvent.builder()
        .commissionId(command.getCommissionId())
        .transactionId(command.getTransactionId())
        .loanLimitId(command.getLoanLimitId())
        .accountId(command.getAccountId())
        .fromWalletId(command.getFromWalletId())
        .toWalletId(command.getToWalletId())
        .cleanAmount(command.getCleanAmount())
        .operationCurrency(command.getOperationCurrency())
        .walletCurrency(command.getWalletCurrency())
        .usedLoanLimit(command.isUsedLoanLimit())
        .usedLoanLimitAmount(command.getUsedLoanLimitAmount())
        .commission(command.getCommission())
        .build();

    AggregateLifecycle.apply(event);
  }

  @CommandHandler
  public void handle(UpdateBankReserveCommand updateBankReserveCommand) {
    BankReserveUpdatedEvent bankReserveUpdatedEvent = new BankReserveUpdatedEvent();
    BeanUtils.copyProperties(updateBankReserveCommand, bankReserveUpdatedEvent);

    AggregateLifecycle.apply(bankReserveUpdatedEvent);
  }
}
