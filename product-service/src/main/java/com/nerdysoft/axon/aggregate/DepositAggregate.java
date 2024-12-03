package com.nerdysoft.axon.aggregate;

import com.nerdysoft.axon.command.bankreserve.UpdateBalanceCommand;
import com.nerdysoft.axon.command.deposit.ApplyDepositCommand;
import com.nerdysoft.axon.command.deposit.CancelWithdrawForDepositCommand;
import com.nerdysoft.axon.command.deposit.DeleteDepositCommand;
import com.nerdysoft.axon.command.deposit.UpdateBankReserveCommand;
import com.nerdysoft.axon.command.deposit.WithdrawDepositCommand;
import com.nerdysoft.axon.command.deposit.WithdrawForDepositCommand;
import com.nerdysoft.axon.command.wallet.WithdrawFromWalletCommand;
import com.nerdysoft.axon.event.bankreserve.BankReserveUpdatedEvent;
import com.nerdysoft.axon.event.deposit.ApplyDepositEvent;
import com.nerdysoft.axon.event.deposit.CancelWithdrawForDepositEvent;
import com.nerdysoft.axon.event.deposit.DepositDeletedEvent;
import com.nerdysoft.axon.event.deposit.WithdrawDepositEvent;
import com.nerdysoft.axon.event.deposit.WithdrawForDepositEvent;
import com.nerdysoft.entity.deposit.Deposit;
import com.nerdysoft.model.enums.Currency;
import com.nerdysoft.model.enums.DepositStatus;
import com.nerdysoft.service.deposit.DepositService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
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
public class DepositAggregate {
    @AggregateIdentifier
    private UUID id;
    private UUID accountId;
    private String accountEmail;
    private UUID walletId;
    private BigDecimal amount;
    private Currency currency;
    private LocalDate depositDate;
    private LocalDate maturityDate;
    private LocalDate notificationDate;
    private BigDecimal yearInterestRate;
    private BigDecimal fourMonthsInterestRate;
    private DepositStatus depositStatus;

    @CommandHandler
    public DepositAggregate(ApplyDepositCommand applyDepositCommand, DepositService depositService) {
        Deposit deposit = depositService.applyDeposit(
                applyDepositCommand.getAccountId(),
                applyDepositCommand.getAccountEmail(),
                applyDepositCommand.getAmount(),
                applyDepositCommand.getCurrency());

        ApplyDepositEvent applyDepositEvent = new ApplyDepositEvent();
        BeanUtils.copyProperties(deposit, applyDepositEvent);

        AggregateLifecycle.apply(applyDepositEvent);
    }

    @EventSourcingHandler
    public void on(ApplyDepositEvent applyDepositEvent) {
        id = applyDepositEvent.getId();
        accountId = applyDepositEvent.getAccountId();
        accountEmail = applyDepositEvent.getAccountEmail();
        walletId = applyDepositEvent.getWalletId();
        amount = applyDepositEvent.getAmount();
        currency = applyDepositEvent.getCurrency();
        depositDate = applyDepositEvent.getDepositDate();
        maturityDate = applyDepositEvent.getMaturityDate();
        notificationDate = applyDepositEvent.getNotificationDate();
        yearInterestRate = applyDepositEvent.getYearInterestRate();
        fourMonthsInterestRate = applyDepositEvent.getFourMonthsInterestRate();
        depositStatus = applyDepositEvent.getDepositStatus();
    }

    @CommandHandler
    public void handle(WithdrawDepositCommand withdrawDepositCommand) {
        WithdrawDepositEvent withdrawDepositEvent = new WithdrawDepositEvent(withdrawDepositCommand.getAccountId());

        AggregateLifecycle.apply(withdrawDepositEvent);
    }

    @EventSourcingHandler
    public void on(WithdrawDepositEvent withdrawDepositEvent) {
        depositStatus = DepositStatus.INACTIVE;
    }

    @CommandHandler
    public void handle(WithdrawForDepositCommand withdrawForDepositCommand, CommandGateway commandGateway) {
        WithdrawFromWalletCommand withdrawFromWalletCommand = new WithdrawFromWalletCommand(
                withdrawForDepositCommand.getWalletId(),
                withdrawForDepositCommand.getAmount(),
                withdrawForDepositCommand.getCurrency()
        );

        CompletableFuture<Object> future = commandGateway.send(withdrawFromWalletCommand);
        try {
            future.get();
        } catch (Exception e) {
            throw new RuntimeException("Can't withdraw for deposit " + e.getMessage());
        }

        WithdrawForDepositEvent withdrawForDepositEvent = new WithdrawForDepositEvent();
        BeanUtils.copyProperties(withdrawForDepositCommand, withdrawForDepositEvent);

        AggregateLifecycle.apply(withdrawForDepositEvent);
    }

    @CommandHandler
    public void handle(UpdateBankReserveCommand updateBankReserveCommand, CommandGateway commandGateway) {
        UpdateBalanceCommand updateBalanceCommand = UpdateBalanceCommand.builder()
                .reserveType(updateBankReserveCommand.getReserveType())
                .amount(updateBankReserveCommand.getAmount())
                .operationType(updateBankReserveCommand.getOperationType())
                .build();

        CompletableFuture<Object> future = commandGateway.send(updateBalanceCommand);
        try {
            future.get();
        } catch (Exception e) {
            throw new RuntimeException("Can't update bank reserve balance " + e.getMessage());
        }

        BankReserveUpdatedEvent bankReserveUpdatedEvent = new BankReserveUpdatedEvent();
        BeanUtils.copyProperties(updateBankReserveCommand, bankReserveUpdatedEvent);

        AggregateLifecycle.apply(bankReserveUpdatedEvent);
    }

    @CommandHandler
    public void handle(DeleteDepositCommand deleteDepositCommand) {
        DepositDeletedEvent depositDeletedEvent = new DepositDeletedEvent(deleteDepositCommand.getId());

        AggregateLifecycle.apply(depositDeletedEvent);
    }

    @EventSourcingHandler
    public void on(DepositDeletedEvent depositDeletedEvent) {
        AggregateLifecycle.markDeleted();
    }

    @CommandHandler
    public void handle(CancelWithdrawForDepositCommand cancelWithdrawForDepositCommand) {
        CancelWithdrawForDepositEvent cancelWithdrawForDepositEvent = new CancelWithdrawForDepositEvent();
        BeanUtils.copyProperties(cancelWithdrawForDepositCommand, cancelWithdrawForDepositEvent);

        AggregateLifecycle.apply(cancelWithdrawForDepositEvent);
    }
}
