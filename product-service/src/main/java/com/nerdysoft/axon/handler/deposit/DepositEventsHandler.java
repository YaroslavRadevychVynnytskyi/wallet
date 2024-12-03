package com.nerdysoft.axon.handler.deposit;

import com.nerdysoft.axon.command.wallet.DepositToWalletCommand;
import com.nerdysoft.axon.event.deposit.CancelWithdrawDepositEvent;
import com.nerdysoft.axon.event.deposit.CancelWithdrawForDepositEvent;
import com.nerdysoft.axon.event.deposit.DepositDeletedEvent;
import com.nerdysoft.axon.event.deposit.WithdrawDepositEvent;
import com.nerdysoft.service.deposit.DepositService;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DepositEventsHandler {
    private final DepositService depositService;
    private final CommandGateway commandGateway;

    @EventHandler
    public void on(WithdrawDepositEvent event) {
        depositService.withdrawDeposit(event.getAccountId());
    }

    @EventHandler
    public void on(CancelWithdrawForDepositEvent cancelWithdrawForDepositEvent) {
        DepositToWalletCommand depositToWalletCommand = new DepositToWalletCommand(
                cancelWithdrawForDepositEvent.getWalletId(),
                cancelWithdrawForDepositEvent.getAmount(),
                cancelWithdrawForDepositEvent.getCurrency()
        );

        commandGateway.send(depositToWalletCommand);
    }

    @EventHandler
    public void on(DepositDeletedEvent depositDeletedEvent) {
        depositService.deleteById(depositDeletedEvent.getId());
    }

    @EventHandler
    public void on(CancelWithdrawDepositEvent cancelWithdrawDepositEvent) {
        depositService.cancelWithdrawDeposit(cancelWithdrawDepositEvent.getId(),
                cancelWithdrawDepositEvent.getAmount(),
                cancelWithdrawDepositEvent.getMaturityDate(),
                cancelWithdrawDepositEvent.getNotificationDate(),
                cancelWithdrawDepositEvent.getDepositStatus());
    }
}
