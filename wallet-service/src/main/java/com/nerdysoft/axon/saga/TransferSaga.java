package com.nerdysoft.axon.saga;

import com.nerdysoft.axon.command.bankreserve.CancelReceiveCommissionCommand;
import com.nerdysoft.axon.command.bankreserve.ReceiveCommissionCommand;
import com.nerdysoft.axon.command.commission.DeleteCommissionCommand;
import com.nerdysoft.axon.command.commission.SaveCommissionCommand;
import com.nerdysoft.axon.command.loanlimit.CancelUpdateLoanLimitAfterTransferCommand;
import com.nerdysoft.axon.command.loanlimit.UpdateLoanLimitAfterTransferCommand;
import com.nerdysoft.axon.command.transaction.SetTransactionFinalStatusCommand;
import com.nerdysoft.axon.command.wallet.CancelTransferToAnotherWalletCommand;
import com.nerdysoft.axon.command.wallet.UpdateReceiveWalletBalanceCommand;
import com.nerdysoft.axon.event.bankreserve.CanceledReceiveCommissionEvent;
import com.nerdysoft.axon.event.bankreserve.ReceivedCommissionEvent;
import com.nerdysoft.axon.event.commission.CommissionDeletedEvent;
import com.nerdysoft.axon.event.commission.CommissionSavedEvent;
import com.nerdysoft.axon.event.loanlimit.CanceledUpdateLoanLimitAfterTransferEvent;
import com.nerdysoft.axon.event.loanlimit.UpdatedLoanLimitAfterTransferEvent;
import com.nerdysoft.axon.event.transaction.TransactionSetFinalStatusEvent;
import com.nerdysoft.axon.event.wallet.CanceledTransferToAnotherWalletEvent;
import com.nerdysoft.axon.event.wallet.TransferredToAnotherWalletEvent;
import com.nerdysoft.axon.event.wallet.UpdatedReceiveWalletBalanceEvent;
import com.nerdysoft.axon.query.loanlimit.FindLoanLimitByAccountIdQuery;
import com.nerdysoft.dto.loanlimit.LoanLimitDto;
import com.nerdysoft.model.enums.ReserveType;
import com.nerdysoft.model.enums.TransactionStatus;
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
public class TransferSaga {
  @Autowired
  private transient CommandGateway commandGateway;

  @Autowired
  private transient QueryGateway queryGateway;

  @StartSaga
  @SagaEventHandler(associationProperty = "transactionId")
  public void handle(TransferredToAnotherWalletEvent event) {
    if (event.isUsedLoanLimit()) {
      queryGateway.query(new FindLoanLimitByAccountIdQuery(event.getAccountId()),
              LoanLimitDto.class)
          .thenApply(LoanLimitDto::getId)
          .thenAccept(
              loanLimitId -> {
                UpdateLoanLimitAfterTransferCommand command = UpdateLoanLimitAfterTransferCommand.builder()
                    .loanLimitId(loanLimitId)
                    .transactionId(event.getTransactionId())
                    .accountId(event.getAccountId())
                    .fromWalletId(event.getFromWalletId())
                    .toWalletId(event.getToWalletId())
                    .cleanAmount(event.getCleanAmount())
                    .operationCurrency(event.getOperationCurrency())
                    .walletCurrency(event.getWalletCurrency())
                    .usedLoanLimit(event.isUsedLoanLimit())
                    .usedLoanLimitAmount(event.getUsedLoanLimitAmount())
                    .commission(event.getCommission())
                    .build();
                commandGateway.sendAndWait(command);
              }
          )
          .exceptionally(e -> {
            CancelTransferToAnotherWalletCommand command = CancelTransferToAnotherWalletCommand.builder()
                .fromWalletId(event.getFromWalletId())
                .transactionId(event.getTransactionId())
                .cleanAmount(event.getCleanAmount())
                .operationCurrency(event.getOperationCurrency())
                .commission(event.getCommission())
                .build();
            return commandGateway.sendAndWait(command);
          });
    } else {
      SaveCommissionCommand command = SaveCommissionCommand.builder()
          .transactionId(event.getTransactionId())
          .accountId(event.getAccountId())
          .fromWalletId(event.getFromWalletId())
          .toWalletId(event.getToWalletId())
          .cleanAmount(event.getCleanAmount())
          .operationCurrency(event.getOperationCurrency())
          .walletCurrency(event.getWalletCurrency())
          .usedLoanLimit(event.isUsedLoanLimit())
          .usedLoanLimitAmount(event.getUsedLoanLimitAmount())
          .commission(event.getCommission())
          .build();
      commandGateway.send(command)
          .exceptionally(e -> {
            CancelTransferToAnotherWalletCommand cancelCommand = CancelTransferToAnotherWalletCommand.builder()
                .fromWalletId(event.getFromWalletId())
                .transactionId(event.getTransactionId())
                .cleanAmount(event.getCleanAmount())
                .operationCurrency(event.getOperationCurrency())
                .commission(event.getCommission())
                .build();
            return commandGateway.sendAndWait(cancelCommand);
          });
    }
  }

  @SagaEventHandler(associationProperty = "transactionId")
  public void handle(UpdatedLoanLimitAfterTransferEvent event) {
    SaveCommissionCommand command = SaveCommissionCommand.builder()
        .transactionId(event.getTransactionId())
        .loanLimitId(event.getLoanLimitId())
        .accountId(event.getAccountId())
        .fromWalletId(event.getFromWalletId())
        .toWalletId(event.getToWalletId())
        .cleanAmount(event.getCleanAmount())
        .operationCurrency(event.getOperationCurrency())
        .walletCurrency(event.getWalletCurrency())
        .usedLoanLimit(event.isUsedLoanLimit())
        .usedLoanLimitAmount(event.getUsedLoanLimitAmount())
        .commission(event.getCommission())
        .build();
    commandGateway.send(command)
        .exceptionally(e -> {
          CancelUpdateLoanLimitAfterTransferCommand cancelCommand = CancelUpdateLoanLimitAfterTransferCommand.builder()
              .loanLimitId(event.getLoanLimitId())
              .transactionId(event.getTransactionId())
              .accountId(event.getAccountId())
              .fromWalletId(event.getFromWalletId())
              .toWalletId(event.getToWalletId())
              .cleanAmount(event.getCleanAmount())
              .operationCurrency(event.getOperationCurrency())
              .walletCurrency(event.getWalletCurrency())
              .usedLoanLimit(event.isUsedLoanLimit())
              .usedLoanLimitAmount(event.getUsedLoanLimitAmount())
              .commission(event.getCommission())
              .build();
          return commandGateway.sendAndWait(cancelCommand);
        });
  }

  @SagaEventHandler(associationProperty = "transactionId")
  public void handle(CommissionSavedEvent event) {
    ReceiveCommissionCommand command = ReceiveCommissionCommand.builder()
        .reserveType(ReserveType.LOAN_LIMIT)
        .commissionId(event.getCommissionId())
        .transactionId(event.getTransactionId())
        .loanLimitId(event.getLoanLimitId())
        .accountId(event.getAccountId())
        .fromWalletId(event.getFromWalletId())
        .toWalletId(event.getToWalletId())
        .cleanAmount(event.getCleanAmount())
        .operationCurrency(event.getOperationCurrency())
        .walletCurrency(event.getWalletCurrency())
        .usedLoanLimit(event.isUsedLoanLimit())
        .usedLoanLimitAmount(event.getUsedLoanLimitAmount())
        .commission(event.getCommission())
        .build();

    commandGateway.send(command)
        .exceptionally(
            e -> {
              DeleteCommissionCommand cancelCommand = DeleteCommissionCommand.builder()
                  .commissionId(event.getCommissionId())
                  .transactionId(event.getTransactionId())
                  .loanLimitId(event.getLoanLimitId())
                  .accountId(event.getAccountId())
                  .fromWalletId(event.getFromWalletId())
                  .toWalletId(event.getToWalletId())
                  .cleanAmount(event.getCleanAmount())
                  .operationCurrency(event.getOperationCurrency())
                  .walletCurrency(event.getWalletCurrency())
                  .usedLoanLimit(event.isUsedLoanLimit())
                  .usedLoanLimitAmount(event.getUsedLoanLimitAmount())
                  .commission(event.getCommission())
                  .build();

              return commandGateway.sendAndWait(cancelCommand);
            });
  }

  @SagaEventHandler(associationProperty = "transactionId")
  public void handle(ReceivedCommissionEvent event) {
    UpdateReceiveWalletBalanceCommand command = UpdateReceiveWalletBalanceCommand.builder()
        .toWalletId(event.getToWalletId())
        .commissionId(event.getCommissionId())
        .transactionId(event.getTransactionId())
        .loanLimitId(event.getLoanLimitId())
        .accountId(event.getAccountId())
        .fromWalletId(event.getFromWalletId())
        .cleanAmount(event.getCleanAmount())
        .operationCurrency(event.getOperationCurrency())
        .walletCurrency(event.getWalletCurrency())
        .usedLoanLimit(event.isUsedLoanLimit())
        .usedLoanLimitAmount(event.getUsedLoanLimitAmount())
        .commission(event.getCommission())
        .build();
    commandGateway.send(command)
        .exceptionally(e -> {
          CancelReceiveCommissionCommand cancelCommand = CancelReceiveCommissionCommand.builder()
              .reserveType(ReserveType.LOAN_LIMIT)
              .commissionId(event.getCommissionId())
              .transactionId(event.getTransactionId())
              .loanLimitId(event.getLoanLimitId())
              .accountId(event.getAccountId())
              .fromWalletId(event.getFromWalletId())
              .toWalletId(event.getToWalletId())
              .cleanAmount(event.getCleanAmount())
              .operationCurrency(event.getOperationCurrency())
              .walletCurrency(event.getWalletCurrency())
              .usedLoanLimit(event.isUsedLoanLimit())
              .usedLoanLimitAmount(event.getUsedLoanLimitAmount())
              .commission(event.getCommission())
              .build();
          return commandGateway.sendAndWait(cancelCommand);
        });
  }

  @SagaEventHandler(associationProperty = "transactionId")
  public void handle(UpdatedReceiveWalletBalanceEvent event) {
    commandGateway.sendAndWait(
        new SetTransactionFinalStatusCommand(event.getTransactionId(), TransactionStatus.SUCCESS));
  }

  @EndSaga
  @SagaEventHandler(associationProperty = "transactionId")
  public void handle(TransactionSetFinalStatusEvent event) {
    log.info(String.format("End of transfer saga. Transaction '%s' with status %s",
        event.getTransactionId(), event.getStatus()));
  }

  @SagaEventHandler(associationProperty = "transactionId")
  public void handle(CanceledTransferToAnotherWalletEvent event) {
    commandGateway.sendAndWait(
        new SetTransactionFinalStatusCommand(event.getTransactionId(), TransactionStatus.FAILURE));
  }

  @SagaEventHandler(associationProperty = "transactionId")
  public void handle(CanceledUpdateLoanLimitAfterTransferEvent event) {
    CancelTransferToAnotherWalletCommand command = CancelTransferToAnotherWalletCommand.builder()
        .fromWalletId(event.getFromWalletId())
        .transactionId(event.getTransactionId())
        .cleanAmount(event.getCleanAmount())
        .operationCurrency(event.getOperationCurrency())
        .commission(event.getCommission())
        .build();
    commandGateway.sendAndWait(command);
  }

  @SagaEventHandler(associationProperty = "transactionId")
  public void handle(CommissionDeletedEvent event) {
    if (event.isUsedLoanLimit()) {
      CancelUpdateLoanLimitAfterTransferCommand cancelCommand = CancelUpdateLoanLimitAfterTransferCommand.builder()
          .loanLimitId(event.getLoanLimitId())
          .transactionId(event.getTransactionId())
          .accountId(event.getAccountId())
          .fromWalletId(event.getFromWalletId())
          .toWalletId(event.getToWalletId())
          .cleanAmount(event.getCleanAmount())
          .operationCurrency(event.getOperationCurrency())
          .walletCurrency(event.getWalletCurrency())
          .usedLoanLimit(event.isUsedLoanLimit())
          .usedLoanLimitAmount(event.getUsedLoanLimitAmount())
          .commission(event.getCommission())
          .build();
      commandGateway.sendAndWait(cancelCommand);
    } else {
      CancelTransferToAnotherWalletCommand command = CancelTransferToAnotherWalletCommand.builder()
          .fromWalletId(event.getFromWalletId())
          .transactionId(event.getTransactionId())
          .cleanAmount(event.getCleanAmount())
          .operationCurrency(event.getOperationCurrency())
          .commission(event.getCommission())
          .build();
      commandGateway.sendAndWait(command);
    }
  }

  @SagaEventHandler(associationProperty = "transactionId")
  public void handle(CanceledReceiveCommissionEvent event) {
    DeleteCommissionCommand cancelCommand = DeleteCommissionCommand.builder()
        .commissionId(event.getCommissionId())
        .transactionId(event.getTransactionId())
        .loanLimitId(event.getLoanLimitId())
        .accountId(event.getAccountId())
        .fromWalletId(event.getFromWalletId())
        .toWalletId(event.getToWalletId())
        .cleanAmount(event.getCleanAmount())
        .operationCurrency(event.getOperationCurrency())
        .walletCurrency(event.getWalletCurrency())
        .usedLoanLimit(event.isUsedLoanLimit())
        .usedLoanLimitAmount(event.getUsedLoanLimitAmount())
        .commission(event.getCommission())
        .build();

    commandGateway.sendAndWait(cancelCommand);
  }
}
