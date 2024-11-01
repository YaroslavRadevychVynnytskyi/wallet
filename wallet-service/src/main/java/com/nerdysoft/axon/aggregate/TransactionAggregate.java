package com.nerdysoft.axon.aggregate;

import com.nerdysoft.axon.command.transaction.CreateTransactionCommand;
import com.nerdysoft.axon.event.transaction.TransactionCreatedEvent;
import com.nerdysoft.dto.request.TransactionRequestDto;
import com.nerdysoft.dto.request.TransferRequestDto;
import com.nerdysoft.model.Transaction;
import com.nerdysoft.model.enums.Currency;
import com.nerdysoft.model.enums.TransactionStatus;
import com.nerdysoft.service.TransactionService;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;

@Aggregate
@NoArgsConstructor
public class TransactionAggregate {
  @AggregateIdentifier
  private UUID transactionId;

  private UUID walletId;

  private UUID toWalletId;

  private BigDecimal walletBalance;

  private BigDecimal amount;

  private Currency currency;

  private TransactionStatus status;

  @CommandHandler
  public TransactionAggregate(CreateTransactionCommand command, TransactionService transactionService) {
    Transaction transaction;
    if (command.getToWalletId() == null) {
      transaction = transactionService.saveTransaction(command.getWalletId(),
          new TransactionRequestDto(command.getAmount(), command.getCurrency()),
          command.getStatus(), command.getWalletBalance());
    } else {
      transaction = transactionService.saveTransaction(command.getWalletId(),
          new TransferRequestDto(command.getToWalletId(), command.getAmount(), command.getCurrency()),
          command.getStatus(), command.getWalletBalance());
    }
    TransactionCreatedEvent event = new TransactionCreatedEvent();
    BeanUtils.copyProperties(transaction, event);
    AggregateLifecycle.apply(event);
  }

  @EventSourcingHandler
  public void on(TransactionCreatedEvent event) {
    this.transactionId = event.getTransactionId();
    this.walletId = event.getWalletId();
    this.toWalletId = event.getToWalletId();
    this.walletBalance = event.getWalletBalance();
    this.amount = event.getAmount();
    this.currency = event.getCurrency();
    this.status = event.getStatus();
  }
}
