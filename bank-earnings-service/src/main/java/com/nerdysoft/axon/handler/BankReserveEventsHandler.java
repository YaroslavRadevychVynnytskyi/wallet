package com.nerdysoft.axon.handler;

import com.nerdysoft.axon.command.bankreserve.UpdateBalanceCommand;
import com.nerdysoft.axon.event.bankreserve.BankReserveUpdatedEvent;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BankReserveEventsHandler {
    private final CommandGateway commandGateway;

    @EventHandler
    public void on(BankReserveUpdatedEvent bankReserveUpdatedEvent) {
        UpdateBalanceCommand updateBalanceCommand = UpdateBalanceCommand.builder()
                .reserveType(bankReserveUpdatedEvent.getReserveType())
                .amount(bankReserveUpdatedEvent.getAmount())
                .operationType(bankReserveUpdatedEvent.getOperationType())
                .build();

        CompletableFuture<Object> future = commandGateway.send(updateBalanceCommand);
        try {
            future.get();
        } catch (Exception e) {
            throw new RuntimeException("Can't update bank reserve balance " + e.getMessage());
        }
    }
}
