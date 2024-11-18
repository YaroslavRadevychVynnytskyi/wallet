package com.nerdysoft.axon.saga;

import com.nerdysoft.axon.command.deposit.CancelUpdateWalletBalanceCommand;
import com.nerdysoft.axon.command.deposit.CancelWithdrawDepositCommand;
import com.nerdysoft.axon.command.deposit.UpdateBankReserveCommand;
import com.nerdysoft.axon.event.bankreserve.BankReserveUpdatedEvent;
import com.nerdysoft.axon.event.deposit.CancelUpdateWalletBalanceEvent;
import com.nerdysoft.axon.event.deposit.CancelWithdrawDepositEvent;
import com.nerdysoft.axon.event.deposit.UpdateWalletBalanceCommand;
import com.nerdysoft.axon.event.deposit.UpdateWalletBalanceEvent;
import com.nerdysoft.axon.event.deposit.WithdrawDepositEvent;
import com.nerdysoft.axon.query.FindAvailableForWithdrawalDepositByAccountIdQuery;
import com.nerdysoft.entity.deposit.Deposit;
import com.nerdysoft.model.enums.OperationType;
import com.nerdysoft.model.enums.ReserveType;
import lombok.extern.log4j.Log4j2;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;

@Saga
@Log4j2
public class WithdrawDepositSaga {
    @Autowired
    private transient CommandGateway commandGateway;

    @Autowired
    private transient QueryGateway queryGateway;

    /**
     * Handles a {@link WithdrawDepositEvent} to process a withdrawal and update the wallet balance.
     * If the deposit retrieval or wallet update fails, a compensating {@link CancelWithdrawDepositCommand}
     * is sent to revert the operation.
     *
     *
     * @param withdrawDepositEvent the event representing the deposit withdrawal request
     */
    @StartSaga
    @SagaEventHandler(associationProperty = "id")
    public void handle(WithdrawDepositEvent withdrawDepositEvent) {
        log.info("Starting withdraw deposit saga for deposit with ID: {}", withdrawDepositEvent.getId());

        Deposit deposit = queryGateway.query(new FindAvailableForWithdrawalDepositByAccountIdQuery(withdrawDepositEvent.getAccountId()), Deposit.class)
                .exceptionally(throwable -> {
                    log.error("Failed to retrieve deposit: {}", throwable.getMessage());

                    CancelWithdrawDepositCommand cancelWithdrawDepositCommand = new CancelWithdrawDepositCommand(withdrawDepositEvent.getId());
                    commandGateway.send(cancelWithdrawDepositCommand);

                    return null;
                })
                .join();

        UpdateWalletBalanceCommand updateWalletBalanceCommand = UpdateWalletBalanceCommand.builder()
                .id(withdrawDepositEvent.getId())
                .walletId(deposit.getWalletId())
                .amount(deposit.getAmount())
                .currency(deposit.getCurrency())
                .operationType(OperationType.DEPOSIT)
                .build();

        commandGateway.send(updateWalletBalanceCommand, (commandMessage, commandResultMessage) -> {
            if (commandResultMessage.isExceptional()) {
                log.error("Compensating transaction for failed deposit to wallet");

                CancelWithdrawDepositCommand cancelWithdrawDepositCommand = new CancelWithdrawDepositCommand(withdrawDepositEvent.getId());
                commandGateway.send(cancelWithdrawDepositCommand);
            }
        });
    }

    /**
     * Handles an {@link UpdateWalletBalanceEvent} to update the bank reserve.
     * If the reserve update fails, compensating {@link CancelUpdateWalletBalanceCommand}
     * and {@link CancelWithdrawDepositCommand} are sent to revert the changes.
     *
     * @param updateWalletBalanceEvent the event containing the wallet balance update details
     */
    @SagaEventHandler(associationProperty = "id")
    public void handle(UpdateWalletBalanceEvent updateWalletBalanceEvent) {
        log.info("Wallet Balance Update successfully executed. Proceeding with bank reserve update...");

        UpdateBankReserveCommand updateBankReserveCommand = UpdateBankReserveCommand.builder()
                .id(updateWalletBalanceEvent.getId())
                .reserveType(ReserveType.DEPOSIT)
                .amount(updateWalletBalanceEvent.getAmount())
                .operationType(OperationType.WITHDRAW)
                .build();

        commandGateway.send(updateBankReserveCommand, (commandMessage, commandResultMessage) -> {
            if (commandResultMessage.isExceptional()) {
                log.error("Bank reserve update failed. Starting compensating transaction...");

                CancelUpdateWalletBalanceCommand cancelUpdateWalletBalanceCommand = CancelUpdateWalletBalanceCommand.builder()
                        .id(updateWalletBalanceEvent.getId())
                        .walletId(updateWalletBalanceEvent.getWalletId())
                        .amount(updateWalletBalanceEvent.getAmount())
                        .currency(updateWalletBalanceEvent.getCurrency())
                        .operationType(OperationType.DEPOSIT)
                        .build();
                commandGateway.send(cancelUpdateWalletBalanceCommand);

                CancelWithdrawDepositCommand cancelWithdrawDepositCommand = new CancelWithdrawDepositCommand(updateWalletBalanceEvent.getId());
                commandGateway.send(cancelWithdrawDepositCommand);
            }
        });
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "id")
    public void handle(BankReserveUpdatedEvent bankReserveUpdatedEvent) {
        log.info("Bank Reserve balance updated successfully. Ending saga");
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "id")
    public void handle(CancelWithdrawDepositEvent cancelWithdrawDepositEvent) {
        log.info("Cancelled deposit withdraw as a compensating transaction. "
                + "Deposit returned to the state at the beginning of the operation");
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "id")
    public void handle(CancelUpdateWalletBalanceEvent cancelUpdateWalletBalanceEvent) {
        log.info("Cancelled wallet update operation as a compensating transaction");
    }
}
