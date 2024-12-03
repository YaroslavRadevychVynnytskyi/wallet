package com.nerdysoft.axon.aggregate;

import com.nerdysoft.axon.command.wallet.CancelTransferToAnotherWalletCommand;
import com.nerdysoft.axon.command.wallet.CancelUpdateWalletBalanceCommand;
import com.nerdysoft.axon.command.wallet.CancelWithdrawFromWalletCommand;
import com.nerdysoft.axon.command.wallet.CreateWalletCommand;
import com.nerdysoft.axon.command.wallet.DeleteWalletCommand;
import com.nerdysoft.axon.command.wallet.DepositToWalletCommand;
import com.nerdysoft.axon.command.wallet.TransferToAnotherWalletCommand;
import com.nerdysoft.axon.command.wallet.UpdateReceiveWalletBalanceCommand;
import com.nerdysoft.axon.command.wallet.UpdateWalletCurrencyCommand;
import com.nerdysoft.axon.command.wallet.WithdrawFromWalletCommand;
import com.nerdysoft.axon.event.wallet.CancelUpdateWalletBalanceEvent;
import com.nerdysoft.axon.event.wallet.CanceledTransferToAnotherWalletEvent;
import com.nerdysoft.axon.event.wallet.CanceledWithdrawFromWalletEvent;
import com.nerdysoft.axon.event.wallet.DepositToWalletSuccessEvent;
import com.nerdysoft.axon.event.wallet.TransferredToAnotherWalletEvent;
import com.nerdysoft.axon.event.wallet.UpdateWalletBalanceCommand;
import com.nerdysoft.axon.event.wallet.UpdateWalletBalanceEvent;
import com.nerdysoft.axon.event.wallet.UpdatedReceiveWalletBalanceEvent;
import com.nerdysoft.axon.event.wallet.UpdatedWalletCurrencyEvent;
import com.nerdysoft.axon.event.wallet.WalletCreatedEvent;
import com.nerdysoft.axon.event.wallet.WalletDeletedEvent;
import com.nerdysoft.axon.event.wallet.WithdrewFromWalletEvent;
import com.nerdysoft.dto.request.CreateWalletDto;
import com.nerdysoft.dto.request.DepositRequestDto;
import com.nerdysoft.dto.request.TransferRequestDto;
import com.nerdysoft.dto.request.WithdrawRequestDto;
import com.nerdysoft.dto.response.DepositResponseDto;
import com.nerdysoft.dto.response.TransferResponseDto;
import com.nerdysoft.dto.response.WithdrawResponseDto;
import com.nerdysoft.entity.Wallet;
import com.nerdysoft.service.WalletService;
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
public class WalletAggregate {
  @AggregateIdentifier
  private UUID walletId;

  @CommandHandler
  public WalletAggregate(CreateWalletCommand command, WalletService walletService) {
    Wallet wallet = walletService.createWallet(
        new CreateWalletDto(command.getAccountId(), command.getCurrency()));
    AggregateLifecycle.apply(new WalletCreatedEvent(wallet.getWalletId()));
  }

  @EventSourcingHandler
  public void on(WalletCreatedEvent event) {
    this.walletId = event.getWalletId();
  }

  @CommandHandler
  public Wallet handle(UpdateWalletCurrencyCommand command, WalletService walletService) {
    Wallet wallet = walletService.updateCurrency(command.getWalletId(), command.getCurrency());
    AggregateLifecycle.apply(
        new UpdatedWalletCurrencyEvent(wallet.getWalletId(), wallet.getBalance(),
            wallet.getCurrency()));
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

    AggregateLifecycle.apply(
        new DepositToWalletSuccessEvent(dto.getTransactionId(), dto.getAccountId(),
            dto.getWalletId(),
            dto.getAmount(), dto.getWalletBalance(), dto.getOperationCurrency()));

    return dto;
  }

  @CommandHandler
  public WithdrawResponseDto handle(WithdrawFromWalletCommand command,
      WalletService walletService) {
    WithdrawRequestDto withdrawRequestDto = WithdrawRequestDto.builder()
        .amount(command.getAmount())
        .currency(command.getCurrency())
        .build();
    WithdrawResponseDto dto = walletService.withdraw(command.getWalletId(), withdrawRequestDto);

    AggregateLifecycle.apply(
        new WithdrewFromWalletEvent(dto.getTransactionId(), dto.getAccountId(), dto.getWalletId(),
            dto.getAmount(), dto.getWalletBalance(), dto.isUsedLoanLimit(),
            dto.getUsedLoanLimitAmount(),
            dto.getOperationCurrency()));

    return dto;
  }

  @CommandHandler
  public void handle(CancelWithdrawFromWalletCommand command, WalletService walletService) {
    BigDecimal balance = walletService.cancelWithdraw(command);

    AggregateLifecycle.apply(
        new CanceledWithdrawFromWalletEvent(command.getWalletId(), command.getTransactionId(),
            balance));
  }

  @CommandHandler
  public TransferResponseDto handle(TransferToAnotherWalletCommand command,
      WalletService walletService) {
    TransferRequestDto transferRequestDto = TransferRequestDto.builder()
        .toWalletId(command.getToWalletId())
        .amount(command.getAmount())
        .currency(command.getCurrency())
        .build();
    TransferResponseDto dto = walletService.transferToAnotherWallet(command.getFromWalletId(),
        transferRequestDto);

    TransferredToAnotherWalletEvent event = TransferredToAnotherWalletEvent.builder()
        .transactionId(dto.getTransactionId())
        .accountId(dto.getAccountId())
        .fromWalletId(dto.getFromWalletId())
        .toWalletId(dto.getToWalletId())
        .cleanAmount(dto.getAmount())
        .operationCurrency(dto.getOperationCurrency())
        .walletCurrency(dto.getWalletCurrency())
        .usedLoanLimit(dto.isUsedLoanLimit())
        .usedLoanLimitAmount(dto.getUsedLoanLimitAmount())
        .commission(dto.getCommission())
        .build();

    AggregateLifecycle.apply(event);

    return dto;
  }

  @CommandHandler
  public void handle(CancelTransferToAnotherWalletCommand command, WalletService walletService) {
    walletService.cancelTransferToAnotherWallet(command.getFromWalletId(), command.getCleanAmount(),
        command.getOperationCurrency(), command.getCommission());

    AggregateLifecycle.apply(new CanceledTransferToAnotherWalletEvent(command.getTransactionId()));
  }

  @CommandHandler
  public void handle(UpdateReceiveWalletBalanceCommand command, WalletService walletService) {
    walletService.updateReceiveWalletBalance(command.getToWalletId(), command.getCleanAmount(),
        command.getOperationCurrency());

    UpdatedReceiveWalletBalanceEvent event = UpdatedReceiveWalletBalanceEvent.builder()
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
