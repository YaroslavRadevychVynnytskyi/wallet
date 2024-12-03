package com.nerdysoft.axon.handler.event;

import com.nerdysoft.axon.command.wallet.DepositToWalletCommand;
import com.nerdysoft.axon.command.wallet.WithdrawFromWalletCommand;
import com.nerdysoft.axon.event.wallet.CancelUpdateWalletBalanceEvent;
import com.nerdysoft.axon.event.wallet.UpdateWalletBalanceEvent;
import com.nerdysoft.model.enums.OperationType;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WalletEventsHandler {
    private final CommandGateway commandGateway;

    @EventHandler
    public void on(CancelUpdateWalletBalanceEvent cancelUpdateWalletBalanceEvent) {
        if (cancelUpdateWalletBalanceEvent.getOperationType().equals(OperationType.WITHDRAW)) {
            DepositToWalletCommand depositToWalletCommand = new DepositToWalletCommand(
                    cancelUpdateWalletBalanceEvent.getWalletId(),
                    cancelUpdateWalletBalanceEvent.getAmount(),
                    cancelUpdateWalletBalanceEvent.getCurrency()
            );

            commandGateway.send(depositToWalletCommand);
        } else if (cancelUpdateWalletBalanceEvent.getOperationType().equals(OperationType.DEPOSIT)) {
            WithdrawFromWalletCommand withdrawFromWalletCommand = new WithdrawFromWalletCommand(
                    cancelUpdateWalletBalanceEvent.getWalletId(),
                    cancelUpdateWalletBalanceEvent.getAmount(),
                    cancelUpdateWalletBalanceEvent.getCurrency()
            );

            commandGateway.send(withdrawFromWalletCommand);
        }
    }

    @EventHandler
    public void on(UpdateWalletBalanceEvent updateWalletBalanceEvent) {
        if (updateWalletBalanceEvent.getOperationType().equals(OperationType.WITHDRAW)) {
            WithdrawFromWalletCommand withdrawFromWalletCommand = new WithdrawFromWalletCommand(
                    updateWalletBalanceEvent.getWalletId(),
                    updateWalletBalanceEvent.getAmount(),
                    updateWalletBalanceEvent.getCurrency()
            );

            CompletableFuture<Object> future = commandGateway.send(withdrawFromWalletCommand);
            try {
                future.get();
            } catch (Exception e) {
                throw new RuntimeException("Can't withdraw for deposit " + e.getMessage());
            }

        } else if (updateWalletBalanceEvent.getOperationType().equals(OperationType.DEPOSIT)) {
            DepositToWalletCommand depositToWalletCommand = new DepositToWalletCommand(
                    updateWalletBalanceEvent.getWalletId(),
                    updateWalletBalanceEvent.getAmount(),
                    updateWalletBalanceEvent.getCurrency()
            );

            CompletableFuture<Object> future = commandGateway.send(depositToWalletCommand);
            try {
                future.get();
            } catch (Exception e) {
                throw new RuntimeException("Can't execute deposit to wallet" + e.getMessage());
            }
        }
    }
}
