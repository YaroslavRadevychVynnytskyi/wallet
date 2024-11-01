package com.nerdysoft.axon.aggregate;

import com.nerdysoft.axon.command.CreateBalanceCommand;
import com.nerdysoft.axon.command.UpdateBalanceCommand;
import com.nerdysoft.axon.event.bankreserve.BalanceCreatedEvent;
import com.nerdysoft.axon.event.bankreserve.BalanceUpdatedEvent;
import com.nerdysoft.dto.api.request.UpdateBalanceDto;
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

        BalanceCreatedEvent balanceCreatedEvent = new BalanceCreatedEvent();
        BeanUtils.copyProperties(bankReserve, balanceCreatedEvent);

        AggregateLifecycle.apply(balanceCreatedEvent);
    }

    @EventSourcingHandler
    public void on(BalanceCreatedEvent balanceCreatedEvent) {
        id = balanceCreatedEvent.getId();
        reserveType = balanceCreatedEvent.getType();
        totalFunds = balanceCreatedEvent.getTotalFunds();
    }

    @CommandHandler
    public UpdateBalanceDto handle(UpdateBalanceCommand updateBalanceCommand, BankReserveService bankReserveService) {
        UpdateBalanceDto updateBalanceDto = updateBalanceCommand.getOperationType().equals(OperationType.DEPOSIT)
            ? bankReserveService.updateBalance(updateBalanceCommand, BigDecimal::add)
            : bankReserveService.updateBalance(updateBalanceCommand, BigDecimal::subtract);
        BalanceUpdatedEvent balanceUpdatedEvent = new BalanceUpdatedEvent();
        BeanUtils.copyProperties(updateBalanceDto, balanceUpdatedEvent);

        AggregateLifecycle.apply(balanceUpdatedEvent);
        return updateBalanceDto;
    }

    @EventSourcingHandler
    public void on(BalanceUpdatedEvent balanceUpdatedEvent) {
        reserveType = balanceUpdatedEvent.getReserveType();

        if (balanceUpdatedEvent.getOperationType().equals(OperationType.WITHDRAW)) {
            totalFunds = totalFunds.subtract(balanceUpdatedEvent.getAmount());
        } else if (balanceUpdatedEvent.getOperationType().equals(OperationType.DEPOSIT)) {
            totalFunds = totalFunds.add(balanceUpdatedEvent.getAmount());
        }
    }
}
