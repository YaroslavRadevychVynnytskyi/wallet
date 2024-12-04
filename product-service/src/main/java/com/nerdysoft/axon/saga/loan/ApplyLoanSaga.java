package com.nerdysoft.axon.saga.loan;

import com.nerdysoft.axon.command.bankreserve.UpdateBankReserveCommand;
import com.nerdysoft.axon.command.loan.DeleteLoanCommand;
import com.nerdysoft.axon.command.loan.HandleRejectedLoanCommand;
import com.nerdysoft.axon.command.wallet.CancelUpdateWalletBalanceCommand;
import com.nerdysoft.axon.event.bankreserve.BankReserveUpdatedEvent;
import com.nerdysoft.axon.event.wallet.CancelUpdateWalletBalanceEvent;
import com.nerdysoft.axon.event.wallet.UpdateWalletBalanceCommand;
import com.nerdysoft.axon.event.wallet.UpdateWalletBalanceEvent;
import com.nerdysoft.axon.event.loan.ApplyLoanEvent;
import com.nerdysoft.axon.event.loan.DeleteLoanEvent;
import com.nerdysoft.axon.event.loan.RejectedLoanEvent;
import com.nerdysoft.model.enums.ApprovalStatus;
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
public class ApplyLoanSaga {
    @Autowired
    private transient CommandGateway commandGateway;

    /**
     * Starts the apply loan saga. Handles the loan application event,
     * initiates wallet balance update, and compensates if update fails.
     *
     * @param applyLoanEvent the event carrying loan application details
     */
    @StartSaga
    @SagaEventHandler(associationProperty = "id")
    public void handle(ApplyLoanEvent applyLoanEvent) {
        log.info("Starting apply loan saga for loan with ID: {}", applyLoanEvent.getId());

        if (applyLoanEvent.getApprovalStatus().equals(ApprovalStatus.REJECTED)) {
            log.error("Loan is rejected");

            HandleRejectedLoanCommand handleRejectedLoanCommand = new HandleRejectedLoanCommand(applyLoanEvent.getId());
            commandGateway.send(handleRejectedLoanCommand);

            return;
        }

        UpdateWalletBalanceCommand updateWalletBalanceCommand = UpdateWalletBalanceCommand.builder()
                .id(applyLoanEvent.getId())
                .walletId(applyLoanEvent.getWalletId())
                .amount(applyLoanEvent.getWalletCurrencyLoanAmount())
                .currency(applyLoanEvent.getWalletCurrency())
                .operationType(OperationType.DEPOSIT)
                .build();

        commandGateway.send(updateWalletBalanceCommand, (commandMessage, commandResultMessage) -> {
            if (commandResultMessage.isExceptional()) {
                log.error("Wallet Balance update failed. Compensating by deleting loan");

                DeleteLoanCommand deleteLoanCommand = new DeleteLoanCommand(applyLoanEvent.getId());
                commandGateway.send(deleteLoanCommand);
            }
        });
    }

    /**
     * Handles wallet balance update event. Initiates bank reserve update
     * and compensates by rolling back wallet changes if reserve update fails.
     *
     * @param updateWalletBalanceEvent the event indicating wallet balance update
     */
    @SagaEventHandler(associationProperty = "id")
    public void handle(UpdateWalletBalanceEvent updateWalletBalanceEvent) {
        log.info("Wallet balance successfully updated. Proceeding with bank reserve update...");

        UpdateBankReserveCommand updateBankReserveCommand = UpdateBankReserveCommand.builder()
                .id(updateWalletBalanceEvent.getId())
                .reserveType(ReserveType.LOAN)
                .amount(updateWalletBalanceEvent.getAmount())
                .operationType(OperationType.WITHDRAW)
                .build();

        commandGateway.send(updateBankReserveCommand, (commandMessage, commandResultMessage) -> {
            if (commandResultMessage.isExceptional()) {
                log.error("Bank Reserve Balance update failed. Compensating by taking money back from wallet and deleting loan");

                CancelUpdateWalletBalanceCommand cancelUpdateWalletBalanceCommand = CancelUpdateWalletBalanceCommand.builder()
                        .id(updateWalletBalanceEvent.getId())
                        .walletId(updateWalletBalanceEvent.getWalletId())
                        .amount(updateWalletBalanceEvent.getAmount())
                        .currency(updateWalletBalanceEvent.getCurrency())
                        .operationType(OperationType.DEPOSIT)
                        .build();
                commandGateway.send(cancelUpdateWalletBalanceCommand);

                DeleteLoanCommand deleteLoanCommand = new DeleteLoanCommand(updateWalletBalanceEvent.getId());
                commandGateway.send(deleteLoanCommand);
            }
        });
    }

    /**
     * Ends the saga upon successful bank reserve update.
     *
     * @param bankReserveUpdatedEvent the event indicating bank reserve update success
     */
    @EndSaga
    @SagaEventHandler(associationProperty = "id")
    public void handle(BankReserveUpdatedEvent bankReserveUpdatedEvent) {
        log.info("Bank Reserve Balance updated successfully. Ending saga");
    }

    /**
     * Ends the saga upon loan rejection.
     *
     * @param rejectedLoanEvent the event indicating loan rejection
     */
    @EndSaga
    @SagaEventHandler(associationProperty = "id")
    public void handle(RejectedLoanEvent rejectedLoanEvent) {
        log.info("Loan with ID: {} is rejected. Ending saga", rejectedLoanEvent.getId());
    }

    /**
     * Ends the saga after successful loan deletion as compensation.
     *
     * @param deleteLoanEvent the event indicating loan deletion
     */
    @EndSaga
    @SagaEventHandler(associationProperty = "id")
    public void handle(DeleteLoanEvent deleteLoanEvent) {
        log.info("Loan deleted successfully as a compensating transaction. Ending saga");
    }

    /**
     * Ends the saga after successfully canceling the wallet balance update as compensation.
     *
     * @param cancelUpdateWalletBalanceEvent the event indicating wallet balance update cancellation
     */
    @EndSaga
    @SagaEventHandler(associationProperty = "id")
    public void handle(CancelUpdateWalletBalanceEvent cancelUpdateWalletBalanceEvent) {
        log.info("Cancelled wallet balance update operation as a compensating transaction. Ending saga");
    }
}
