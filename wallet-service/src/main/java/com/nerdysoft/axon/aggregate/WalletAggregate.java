package com.nerdysoft.axon.aggregate;

import com.nerdysoft.axon.command.wallet.CancelUpdateWalletBalanceCommand;
import com.nerdysoft.axon.command.wallet.CreateWalletCommand;
import com.nerdysoft.axon.command.wallet.DeleteWalletCommand;
import com.nerdysoft.axon.command.wallet.DepositToWalletCommand;
import com.nerdysoft.axon.command.wallet.TransferToAnotherWalletCommand;
import com.nerdysoft.axon.command.wallet.UpdateBalanceForReceiverWalletCommand;
import com.nerdysoft.axon.command.wallet.UpdateWalletCurrencyCommand;
import com.nerdysoft.axon.command.wallet.WithdrawFromWalletCommand;
import com.nerdysoft.axon.event.wallet.CancelUpdateWalletBalanceEvent;
import com.nerdysoft.axon.event.wallet.TransactionFailureEvent;
import com.nerdysoft.axon.event.wallet.TransactionSuccessEvent;
import com.nerdysoft.axon.event.wallet.TransferFailureEvent;
import com.nerdysoft.axon.event.wallet.TransferSuccessEvent;
import com.nerdysoft.axon.event.wallet.UpdateWalletBalanceCommand;
import com.nerdysoft.axon.event.wallet.UpdateWalletBalanceEvent;
import com.nerdysoft.axon.event.wallet.UpdatedWalletCurrencyEvent;
import com.nerdysoft.axon.event.wallet.WalletCreatedEvent;
import com.nerdysoft.axon.event.wallet.WalletDeletedEvent;
import com.nerdysoft.dto.request.CreateWalletDto;
import com.nerdysoft.dto.request.TransactionRequestDto;
import com.nerdysoft.dto.request.TransferRequestDto;
import com.nerdysoft.dto.response.TransactionResponseDto;
import com.nerdysoft.dto.response.TransferResponseDto;
import com.nerdysoft.entity.Wallet;
import com.nerdysoft.model.enums.Currency;
import com.nerdysoft.model.enums.TransactionStatus;
import com.nerdysoft.service.WalletService;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.function.BiFunction;
import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;

@Aggregate
@NoArgsConstructor
public class WalletAggregate {
  @AggregateIdentifier
  private UUID walletId;

  private BigDecimal balance;

  private Currency currency;

  @CommandHandler
  public WalletAggregate(CreateWalletCommand command, WalletService walletService) {
    Wallet wallet = walletService.createWallet(new CreateWalletDto(command.getAccountId(), command.getCurrency()));
    AggregateLifecycle.apply(new WalletCreatedEvent(wallet.getWalletId(), wallet.getCurrency()));
  }

  @EventSourcingHandler
  public void on(WalletCreatedEvent event) {
    this.walletId = event.getWalletId();
    balance = BigDecimal.ZERO;
    currency = event.getCurrency();
  }

  @CommandHandler
  public Wallet handle(UpdateWalletCurrencyCommand command, WalletService walletService) {
    Wallet wallet = walletService.updateCurrency(command.getWalletId(), command.getCurrency());
    AggregateLifecycle.apply(new UpdatedWalletCurrencyEvent(wallet.getWalletId(), wallet.getBalance(), wallet.getCurrency()));
    return wallet;
  }

  @EventSourcingHandler
  public void on(UpdatedWalletCurrencyEvent event) {
    balance = event.getBalance();
    currency = event.getCurrency();
  }

  @CommandHandler
  public String handle(DeleteWalletCommand command, WalletService walletService) {
    String message = walletService.deleteById(command.getWalletId());
    AggregateLifecycle.apply(new WalletDeletedEvent(command.getWalletId()));
    return message;
  }

  @EventSourcingHandler
  public void on(WalletDeletedEvent event) {
    AggregateLifecycle.markDeleted();
  }

  @CommandHandler
  public TransactionResponseDto handle(DepositToWalletCommand command, WalletService walletService) {
    return handleTransaction(command.getWalletId(), command.getAmount(), command.getCurrency(),
        BigDecimal::add, walletService);
  }

  @CommandHandler
  public TransactionResponseDto handle(WithdrawFromWalletCommand command, WalletService walletService) {
    return handleTransaction(command.getWalletId(), command.getAmount(), command.getCurrency(),
        BigDecimal::subtract, walletService);
  }

  private TransactionResponseDto handleTransaction(UUID walletId, BigDecimal amount, Currency currency,
      BiFunction<BigDecimal, BigDecimal, BigDecimal> operation, WalletService walletService) {
    TransactionResponseDto dto = walletService.transaction(walletId, new TransactionRequestDto(amount, currency), operation);

    if (dto.status().equals(TransactionStatus.SUCCESS)) {
      AggregateLifecycle.apply(new TransactionSuccessEvent(dto.walletId(), dto.walletBalance(), dto.amount(), dto.currency(), dto.transactionId()));
    } else {
      AggregateLifecycle.apply(new TransactionFailureEvent(dto.walletId(), dto.amount(), dto.currency(), dto.transactionId()));
    }

    return dto;
  }

  @EventSourcingHandler
  public void on(TransactionSuccessEvent event) {
    balance = event.getBalance();
  }

  @CommandHandler
  public TransferResponseDto handle(TransferToAnotherWalletCommand command, WalletService walletService,
      CommandGateway commandGateway) {
    TransferResponseDto dto = walletService.transferToAnotherWallet(command.getFromWalletId(),
        new TransferRequestDto(command.getToWalletId(), command.getAmount(), command.getCurrency()));
    if (dto.status().equals(TransactionStatus.SUCCESS)) {
      Wallet receiverWallet = walletService.findById(dto.toWalletId());
      commandGateway.sendAndWait(new UpdateBalanceForReceiverWalletCommand(receiverWallet.getWalletId(), receiverWallet.getBalance()));
      AggregateLifecycle.apply(new TransferSuccessEvent(dto.fromWalletId(), dto.toWalletId(), dto.walletBalance(),
          receiverWallet.getBalance(), dto.amount(), dto.currency(), dto.transactionId()));
    } else {
      AggregateLifecycle.apply(new TransferFailureEvent(dto.fromWalletId(), dto.toWalletId(), dto.amount(), dto.currency(), dto.transactionId()));
    }
    return dto;
  }

  @EventSourcingHandler
  public void on(TransferSuccessEvent event) {
    balance = event.getFromWalletBalance();
  }

  @CommandHandler
  public void handle(UpdateBalanceForReceiverWalletCommand command) {
    balance = command.getBalance();
  }

  @CommandHandler
  public void handle(UpdateWalletBalanceCommand updateWalletBalanceCommand) {
    UpdateWalletBalanceEvent updateWalletBalanceEvent = new UpdateWalletBalanceEvent();
    BeanUtils.copyProperties(updateWalletBalanceCommand, updateWalletBalanceEvent);

    AggregateLifecycle.apply(updateWalletBalanceEvent);
  }

  @CommandHandler
  public void handle(CancelUpdateWalletBalanceCommand cancelUpdateWalletBalanceCommand) {
    CancelUpdateWalletBalanceEvent cancelUpdateWalletBalanceEvent = new CancelUpdateWalletBalanceEvent();
    BeanUtils.copyProperties(cancelUpdateWalletBalanceCommand, cancelUpdateWalletBalanceEvent);

    AggregateLifecycle.apply(cancelUpdateWalletBalanceEvent);
  }
}
