package com.nerdysoft.axon.aggregate;

import com.nerdysoft.axon.command.wallet.CancelWithdrawFromWalletCommand;
import com.nerdysoft.axon.command.wallet.CreateWalletCommand;
import com.nerdysoft.axon.command.wallet.DeleteWalletCommand;
import com.nerdysoft.axon.command.wallet.DepositToWalletCommand;
import com.nerdysoft.axon.command.wallet.TransferToAnotherWalletCommand;
import com.nerdysoft.axon.command.wallet.UpdateWalletCurrencyCommand;
import com.nerdysoft.axon.command.wallet.WithdrawFromWalletCommand;
import com.nerdysoft.axon.event.wallet.CanceledWithdrawFromWalletEvent;
import com.nerdysoft.axon.event.wallet.DepositToWalletFailureEvent;
import com.nerdysoft.axon.event.wallet.DepositToWalletSuccessEvent;
import com.nerdysoft.axon.event.wallet.UpdatedWalletCurrencyEvent;
import com.nerdysoft.axon.event.wallet.WalletCreatedEvent;
import com.nerdysoft.axon.event.wallet.WalletDeletedEvent;
import com.nerdysoft.axon.event.wallet.WithdrawFromWalletFailureEvent;
import com.nerdysoft.axon.event.wallet.WithdrawFromWalletSuccessEvent;
import com.nerdysoft.dto.request.CreateWalletDto;
import com.nerdysoft.dto.request.DepositRequestDto;
import com.nerdysoft.dto.request.WithdrawRequestDto;
import com.nerdysoft.dto.response.DepositResponseDto;
import com.nerdysoft.dto.response.TransferResponseDto;
import com.nerdysoft.dto.response.WithdrawResponseDto;
import com.nerdysoft.entity.Wallet;
import com.nerdysoft.model.enums.TransactionStatus;
import com.nerdysoft.service.WalletService;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

@Aggregate
@NoArgsConstructor
public class WalletAggregate {
  @AggregateIdentifier
  private UUID walletId;

  @CommandHandler
  public WalletAggregate(CreateWalletCommand command, WalletService walletService) {
    Wallet wallet = walletService.createWallet(new CreateWalletDto(command.getAccountId(), command.getCurrency()));
    AggregateLifecycle.apply(new WalletCreatedEvent(wallet.getWalletId()));
  }

  @EventSourcingHandler
  public void on(WalletCreatedEvent event) {
    this.walletId = event.getWalletId();
  }

  @CommandHandler
  public Wallet handle(UpdateWalletCurrencyCommand command, WalletService walletService) {
    Wallet wallet = walletService.updateCurrency(command.getWalletId(), command.getCurrency());
    AggregateLifecycle.apply(new UpdatedWalletCurrencyEvent(wallet.getWalletId(), wallet.getBalance(), wallet.getCurrency()));
    return wallet;
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
  public DepositResponseDto handle(DepositToWalletCommand command, WalletService walletService) {
    DepositRequestDto depositRequestDto = DepositRequestDto.builder()
        .amount(command.getAmount())
        .currency(command.getCurrency())
        .build();
    DepositResponseDto dto = walletService.deposit(command.getWalletId(), depositRequestDto);

    if (dto.getStatus().equals(TransactionStatus.SUCCESS)) {
      AggregateLifecycle.apply(new DepositToWalletSuccessEvent(dto.getTransactionId(), dto.getWalletId(),
          dto.getAmount(), dto.getWalletBalance(), dto.getOperationCurrency()));
    } else {
      AggregateLifecycle.apply(new DepositToWalletFailureEvent(dto.getTransactionId(), dto.getWalletId(),
          dto.getAmount(), dto.getWalletBalance(), dto.getOperationCurrency()));
    }

    return dto;
  }

  @CommandHandler
  public WithdrawResponseDto handle(WithdrawFromWalletCommand command, WalletService walletService) {
    WithdrawRequestDto withdrawRequestDto = WithdrawRequestDto.builder()
        .amount(command.getAmount())
        .currency(command.getCurrency())
        .build();
    WithdrawResponseDto dto = walletService.withdraw(command.getWalletId(), withdrawRequestDto);

    if (dto.getStatus().equals(TransactionStatus.PENDING)) {
      AggregateLifecycle.apply(new WithdrawFromWalletSuccessEvent(dto.getTransactionId(), dto.getWalletId(),
          dto.getAmount(), dto.getWalletBalance(), dto.isUsedLoanLimit(), dto.getUsedLoanLimitAmount(),
          dto.getOperationCurrency()));
    } else if (dto.getStatus().equals(TransactionStatus.FAILURE)) {
      AggregateLifecycle.apply(new WithdrawFromWalletFailureEvent(dto.getTransactionId(), dto.getWalletId(),
          dto.getAmount(), dto.getWalletBalance(), dto.isUsedLoanLimit(), dto.getUsedLoanLimitAmount(),
          dto.getOperationCurrency()));
    }

    return dto;
  }

  @CommandHandler
  public void handle(CancelWithdrawFromWalletCommand command, WalletService walletService) {
    BigDecimal balance = walletService.cancelWithdraw(command);

    AggregateLifecycle.apply(new CanceledWithdrawFromWalletEvent(command.getWalletId(), command.getTransactionId(),
        balance));
  }

  @CommandHandler
  public TransferResponseDto handle(TransferToAnotherWalletCommand command, WalletService walletService,
      CommandGateway commandGateway) {
    TransferResponseDto dto = null;
//    TransferResponseDto dto = walletService.transferToAnotherWallet(command.getFromWalletId(),
//        new TransferRequestDto(command.getToWalletId(), command.getAmount(), command.getCurrency()));
//    if (dto.status().equals(TransactionStatus.SUCCESS)) {
//      Wallet receiverWallet = walletService.findById(dto.toWalletId());
//      commandGateway.sendAndWait(new UpdateBalanceForReceiverWalletCommand(receiverWallet.getWalletId(), receiverWallet.getBalance()));
//      AggregateLifecycle.apply(new TransferSuccessEvent(dto.fromWalletId(), dto.toWalletId(), dto.walletBalance(),
//          receiverWallet.getBalance(), dto.amount(), dto.currency(), dto.transactionId()));
//    } else {
//      AggregateLifecycle.apply(new TransferFailureEvent(dto.fromWalletId(), dto.toWalletId(), dto.amount(), dto.currency(), dto.transactionId()));
//    }
    return dto;
  }
}
