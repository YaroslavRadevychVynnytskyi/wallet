package com.nerdysoft.axon.aggregate;

import com.nerdysoft.axon.command.CreateBalanceCommand;
import com.nerdysoft.axon.command.UpdateBalanceCommand;
import com.nerdysoft.axon.event.bankreserve.BalanceCreatedEvent;
import com.nerdysoft.axon.event.bankreserve.BalanceUpdatedEvent;
import com.nerdysoft.dto.api.response.UpdateBalanceResponseDto;
import com.nerdysoft.model.enums.OperationType;
import com.nerdysoft.model.enums.ReserveType;
import com.nerdysoft.model.exception.UniqueException;
import com.nerdysoft.model.reserve.BankReserve;
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
import org.springframework.http.HttpStatus;

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
    public UpdateBalanceResponseDto handle(UpdateBalanceCommand updateBalanceCommand, BankReserveService bankReserveService) {
        if (updateBalanceCommand.getReserveType().equals(reserveType)) {
            UpdateBalanceResponseDto updateBalanceResponseDto;

            if (updateBalanceCommand.getOperationType().equals(OperationType.DEPOSIT)) {
                updateBalanceResponseDto = bankReserveService.updateBalance(updateBalanceCommand, BigDecimal::add);
            } else {
                updateBalanceResponseDto = bankReserveService.updateBalance(updateBalanceCommand, BigDecimal::subtract);
            }

            BalanceUpdatedEvent updateBalanceEvent = new BalanceUpdatedEvent();
            BeanUtils.copyProperties(updateBalanceResponseDto, updateBalanceEvent);

            AggregateLifecycle.apply(updateBalanceEvent);
            return updateBalanceResponseDto;
        } else {
            throw new UniqueException(
                String.format("No %s reserves exists with id '%s'", updateBalanceCommand.getReserveType(), updateBalanceCommand.getId()),
                HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @EventSourcingHandler
    public void on(BalanceUpdatedEvent updateBalanceEvent) {
        totalFunds = updateBalanceEvent.getBalance();
    }
}
