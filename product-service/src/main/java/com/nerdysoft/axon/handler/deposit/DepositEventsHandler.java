package com.nerdysoft.axon.handler.deposit;

import com.nerdysoft.axon.event.deposit.WithdrawDepositEvent;
import com.nerdysoft.service.deposit.DepositService;
import lombok.RequiredArgsConstructor;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DepositEventsHandler {
    private final DepositService depositService;

    @EventHandler
    public void on(WithdrawDepositEvent event) {
        depositService.withdrawDeposit(event.getAccountId());
    }
}
