package com.nerdysoft.axon.aggregate;

import com.nerdysoft.axon.command.transaction.CreateTransactionCommand;
import com.nerdysoft.axon.command.transaction.SetTransactionFinalStatusCommand;
import com.nerdysoft.axon.event.transaction.TransactionCreatedEvent;
import com.nerdysoft.axon.event.transaction.TransactionSetFinalStatusEvent;
import com.nerdysoft.entity.Transaction;
import com.nerdysoft.service.TransactionService;
import java.util.UUID;
import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

@Aggregate
@NoArgsConstructor
public class TransactionAggregate {
  @AggregateIdentifier
  private UUID transactionId;

  @CommandHandler
  public TransactionAggregate(CreateTransactionCommand command, TransactionService transactionService) {
    Transaction transaction = transactionService.saveTransaction(command);

    AggregateLifecycle.apply(new TransactionCreatedEvent(transaction.getTransactionId()));
  }

  @EventSourcingHandler
  public void on(TransactionCreatedEvent event) {
    this.transactionId = event.getTransactionId();
  }

  @CommandHandler
  public void handle(SetTransactionFinalStatusCommand command, TransactionService transactionService) {
    Transaction transaction = transactionService.updateTransactionStatus(command.getTransactionId(), command.getStatus());

    AggregateLifecycle.apply(new TransactionSetFinalStatusEvent(transaction.getTransactionId(), transaction.getStatus()));
  }

  @EventSourcingHandler
  public void on(TransactionSetFinalStatusEvent event) {
    AggregateLifecycle.markDeleted();
  }
}
