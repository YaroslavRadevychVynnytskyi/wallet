package com.nerdysoft.axon.aggregate;

import com.nerdysoft.axon.command.CreateBalanceCommand;
import com.nerdysoft.axon.command.UpdateBalanceCommand;
import com.nerdysoft.axon.event.bankreserve.BalanceCreatedEvent;
import com.nerdysoft.axon.event.bankreserve.BalanceUpdatedEvent;
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
    private Integer id;
    private ReserveType reserveType;
    private BigDecimal totalFunds;

    @CommandHandler
    public BankReserveAggregate(CreateBalanceCommand createBalanceCommand, BankReserveService bankReserveService) {
        BankReserve bankReserve = bankReserveService.create(createBalanceCommand);

        BalanceCreatedEvent createBalanceEvent = new BalanceCreatedEvent();
        BeanUtils.copyProperties(bankReserve, createBalanceEvent);

        AggregateLifecycle.apply(createBalanceEvent);
    }

    @EventSourcingHandler
    public void on(BalanceCreatedEvent balanceCreatedEvent) {
        id = balanceCreatedEvent.getId();
        reserveType = balanceCreatedEvent.getType();
        totalFunds = balanceCreatedEvent.getTotalFunds();
    }

    @CommandHandler
    public void handle(UpdateBalanceCommand updateBalanceCommand) {
        BalanceUpdatedEvent updateBalanceEvent = new BalanceUpdatedEvent();
        BeanUtils.copyProperties(updateBalanceCommand, updateBalanceEvent);

        AggregateLifecycle.apply(updateBalanceEvent);
    }

    @EventSourcingHandler
    public void on(BalanceUpdatedEvent updateBalanceEvent) {
        reserveType = updateBalanceEvent.getReserveType();

        if (updateBalanceEvent.getOperationType().equals(OperationType.WITHDRAW)) {
            totalFunds = totalFunds.subtract(updateBalanceEvent.getAmount());
        } else if (updateBalanceEvent.getOperationType().equals(OperationType.DEPOSIT)) {
            totalFunds = totalFunds.add(updateBalanceEvent.getAmount());
        }
    }
}
