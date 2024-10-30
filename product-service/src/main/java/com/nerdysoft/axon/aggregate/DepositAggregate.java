package com.nerdysoft.axon.aggregate;

import com.nerdysoft.axon.command.deposit.ApplyDepositCommand;
import com.nerdysoft.axon.command.deposit.WithdrawDepositCommand;
import com.nerdysoft.axon.event.deposit.ApplyDepositEvent;
import com.nerdysoft.axon.event.deposit.WithdrawDepositEvent;
import com.nerdysoft.entity.deposit.Deposit;
import com.nerdysoft.model.enums.Currency;
import com.nerdysoft.model.enums.DepositStatus;
import com.nerdysoft.service.deposit.DepositService;
import java.math.BigDecimal;
import java.time.LocalDate;
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
                applyDepositCommand.getWalletCurrency()
        );

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
}
