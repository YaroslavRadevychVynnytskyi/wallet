package com.nerdysoft.axon.saga;

import com.nerdysoft.axon.command.deposit.CancelWithdrawForDepositCommand;
import com.nerdysoft.axon.command.deposit.DeleteDepositCommand;
import com.nerdysoft.axon.command.deposit.UpdateBankReserveCommand;
import com.nerdysoft.axon.command.deposit.WithdrawForDepositCommand;
import com.nerdysoft.axon.event.bankreserve.BankReserveUpdatedEvent;
import com.nerdysoft.axon.event.deposit.ApplyDepositEvent;
import com.nerdysoft.axon.event.deposit.CancelWithdrawForDepositEvent;
import com.nerdysoft.axon.event.deposit.DepositDeletedEvent;
import com.nerdysoft.axon.event.deposit.UpdateWalletBalanceCommand;
import com.nerdysoft.axon.event.deposit.UpdateWalletBalanceEvent;
import com.nerdysoft.model.enums.OperationType;
import com.nerdysoft.model.enums.ReserveType;
import lombok.extern.log4j.Log4j2;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;

@Saga
@Log4j2
public class ApplyDepositSaga {
    @Autowired
    private transient CommandGateway commandGateway;

    /**
     * Handles the initial event for applying a deposit, starting the saga process.
     * Sends a {@link WithdrawForDepositCommand} to withdraw the specified amount from the user's wallet.
     * If the withdrawal fails, initiates a compensating transaction by issuing a {@link DeleteDepositCommand}
     * to remove the deposit entry.
     *
     * @param event the {@link ApplyDepositEvent} containing details of the deposit application.
     */
    @StartSaga
    @SagaEventHandler(associationProperty = "id")
    public void handle(ApplyDepositEvent event) {
        log.info("Starting saga for deposit with ID: {}", event.getId());

        UpdateWalletBalanceCommand updateWalletBalanceCommand = UpdateWalletBalanceCommand.builder()
                .id(event.getId())
                .walletId(event.getWalletId())
                .amount(event.getAmount())
                .currency(event.getCurrency())
                .operationType(OperationType.WITHDRAW)
                .build();

        commandGateway.send(updateWalletBalanceCommand, (commandMessage, commandResultMessage) -> {
            if (commandResultMessage.isExceptional()) {
                log.error("Compensating transaction for failed withdrawal. Deleting deposit with ID: {}", event.getId());

                DeleteDepositCommand deleteDepositCommand = new DeleteDepositCommand(event.getId());
                commandGateway.send(deleteDepositCommand);
            }
        });
    }

    /**
     * Handles the withdrawal confirmation event within the deposit saga.
     * Sends an {@link UpdateBankReserveCommand} to update the bank's reserve balance with the deposited amount.
     * If the update fails, initiates a compensating transaction by issuing a {@link CancelWithdrawForDepositCommand}
     * to reverse the wallet withdrawal and a {@link DeleteDepositCommand} to delete the deposit entry.
     *
     * @param updateWalletBalanceEvent the {@link UpdateWalletBalanceEvent} confirming the successful withdrawal.
     */
    @SagaEventHandler(associationProperty = "id")
    public void handle(UpdateWalletBalanceEvent updateWalletBalanceEvent) {
        log.info("Withdraw for deposit executed. Proceeding with bank reserve update...");

        UpdateBankReserveCommand updateBankReserveCommand = UpdateBankReserveCommand.builder()
                .id(updateWalletBalanceEvent.getId())
                .reserveType(ReserveType.DEPOSIT)
                .amount(updateWalletBalanceEvent.getAmount())
                .operationType(OperationType.DEPOSIT)
                .build();

        commandGateway.send(updateBankReserveCommand, (commandMessage, commandResultMessage) -> {
            if (commandResultMessage.isExceptional()) {
                log.error("Bank reserve update failed. Compensating by returning money to wallet and deleting deposit...");

                CancelWithdrawForDepositCommand cancelWithdrawForDepositCommand = CancelWithdrawForDepositCommand.builder()
                        .id(updateWalletBalanceEvent.getId())
                        .walletId(updateWalletBalanceEvent.getWalletId())
                        .amount(updateWalletBalanceEvent.getAmount())
                        .currency(updateWalletBalanceEvent.getCurrency())
                        .build();
                commandGateway.send(cancelWithdrawForDepositCommand);

                DeleteDepositCommand deleteDepositCommand = new DeleteDepositCommand(updateWalletBalanceEvent.getId());
                commandGateway.send(deleteDepositCommand);
            }
        });
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "id")
    public void handle(BankReserveUpdatedEvent bankReserveUpdatedEvent) {
        log.info("Bank Reserve balance updated successfully. Ending saga");
    }

    @SagaEventHandler(associationProperty = "id")
    public void handle(CancelWithdrawForDepositEvent cancelWithdrawForDepositEvent) {
        log.info("Cancelled withdraw for deposit as a compensating transaction.");
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "id")
    public void handle(DepositDeletedEvent depositDeletedEvent) {
        log.info("Deposit deleted successfully as a compensating transaction. Ending saga");
    }
}
