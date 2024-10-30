package com.nerdysoft.axon.handler;

import com.nerdysoft.axon.event.UpdateBalanceEvent;
import com.nerdysoft.dto.api.request.UpdateBalanceDto;
import com.nerdysoft.model.enums.OperationType;
import com.nerdysoft.service.BankReserveService;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BankReserveEventsHandler {
    private final BankReserveService bankReserveService;

    @EventHandler
    public void on(UpdateBalanceEvent updateBalanceEvent) {
        UpdateBalanceDto updatedBalance = updateBalanceEvent.getOperationType().equals(OperationType.DEPOSIT)
                ? bankReserveService.updateBalance(updateBalanceEvent, BigDecimal::add)
                : bankReserveService.updateBalance(updateBalanceEvent, BigDecimal::subtract);
    }
}
