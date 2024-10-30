package com.nerdysoft.axon.aggregate;

import com.nerdysoft.axon.command.CreateBalanceCommand;
import com.nerdysoft.axon.command.UpdateBalanceCommand;
import com.nerdysoft.axon.event.CreateBalanceEvent;
import com.nerdysoft.axon.event.UpdateBalanceEvent;
import com.nerdysoft.entity.reserve.BankReserve;
import com.nerdysoft.entity.reserve.enums.ReserveType;
import com.nerdysoft.model.enums.OperationType;
import com.nerdysoft.service.BankReserveService;
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
public class BankReserveAggregate {
    @AggregateIdentifier
    private UUID id;
    private ReserveType reserveType;
    private BigDecimal totalFunds;

    @CommandHandler
    public BankReserveAggregate(CreateBalanceCommand createBalanceCommand, BankReserveService bankReserveService) {
        BankReserve bankReserve = bankReserveService.create(createBalanceCommand);

        CreateBalanceEvent createBalanceEvent = new CreateBalanceEvent();
        BeanUtils.copyProperties(bankReserve, createBalanceEvent);

        AggregateLifecycle.apply(createBalanceEvent);
    }

    @EventSourcingHandler
    public void on(CreateBalanceEvent createBalanceEvent) {
        id = createBalanceEvent.getId();
        reserveType = createBalanceEvent.getType();
        totalFunds = createBalanceEvent.getTotalFunds();
    }

    @CommandHandler
    public void handle(UpdateBalanceCommand updateBalanceCommand) {
        UpdateBalanceEvent updateBalanceEvent = new UpdateBalanceEvent();
        BeanUtils.copyProperties(updateBalanceCommand, updateBalanceEvent);

        AggregateLifecycle.apply(updateBalanceEvent);
    }

    @EventSourcingHandler
    public void on(UpdateBalanceEvent updateBalanceEvent) {
        reserveType = updateBalanceEvent.getReserveType();

        if (updateBalanceEvent.getOperationType().equals(OperationType.WITHDRAW)) {
            totalFunds = totalFunds.subtract(updateBalanceEvent.getAmount());
        } else if (updateBalanceEvent.getOperationType().equals(OperationType.DEPOSIT)) {
            totalFunds = totalFunds.add(updateBalanceEvent.getAmount());
        }
    }
}
