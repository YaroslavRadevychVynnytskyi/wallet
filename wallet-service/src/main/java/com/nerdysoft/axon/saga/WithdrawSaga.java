package com.nerdysoft.axon.saga;

import com.nerdysoft.axon.command.loanlimit.CancelSubtractionFromLoanLimitCommand;
import com.nerdysoft.axon.command.loanlimit.SubtractFromLoanLimitCommand;
import com.nerdysoft.axon.command.transaction.SetTransactionFinalStatusCommand;
import com.nerdysoft.axon.command.wallet.CancelWithdrawFromWalletCommand;
import com.nerdysoft.axon.event.loanlimit.CanceledSubtractionFromLoanLimitEvent;
import com.nerdysoft.axon.event.loanlimit.SubtractedFromLoanLimitEvent;
import com.nerdysoft.axon.event.transaction.TransactionSetFinalStatusEvent;
import com.nerdysoft.axon.event.wallet.CanceledWithdrawFromWalletEvent;
import com.nerdysoft.axon.event.wallet.WithdrawFromWalletSuccessEvent;
import com.nerdysoft.axon.query.loanlimit.FindLoanLimitByWalletIdQuery;
import com.nerdysoft.dto.loanlimit.LoanLimitDto;
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
public class WithdrawSaga {
  @Autowired
  private transient CommandGateway commandGateway;

  @Autowired
  private transient QueryGateway queryGateway;

  @StartSaga
  @SagaEventHandler(associationProperty = "transactionId")
  public void handle(WithdrawFromWalletSuccessEvent event) {
    if (event.isUsedLoanLimit()) {
      queryGateway.query(new FindLoanLimitByWalletIdQuery(event.getWalletId()), LoanLimitDto.class)
          .thenApply(LoanLimitDto::getId)
          .thenAccept(loanLimitId -> commandGateway.send(new SubtractFromLoanLimitCommand(
                      loanLimitId,
                      event.getTransactionId(),
                      event.getUsedLoanLimitAmount(),
                      event.getWalletId(),
                      event.getAmount()
                  ))
                  .exceptionally(e -> commandGateway.sendAndWait(new CancelWithdrawFromWalletCommand(
                      event.getWalletId(),
                      event.getTransactionId(),
                      event.getAmount(),
                      event.getUsedLoanLimitAmount()
                  )))
          );
    } else {
      commandGateway.send(
              new SetTransactionFinalStatusCommand(event.getTransactionId(), TransactionStatus.SUCCESS))
          .exceptionally(e -> commandGateway.sendAndWait(new CancelWithdrawFromWalletCommand(
              event.getWalletId(),
              event.getTransactionId(),
              event.getAmount(),
              event.getUsedLoanLimitAmount()
          )));
    }
  }

  @SagaEventHandler(associationProperty = "transactionId")
  public void handle(SubtractedFromLoanLimitEvent event) {
    commandGateway.send(
            new SetTransactionFinalStatusCommand(event.getTransactionId(), TransactionStatus.SUCCESS))
        .exceptionally(e -> commandGateway.sendAndWait(new CancelSubtractionFromLoanLimitCommand(
            event.getLoanLimitId(),
            event.getTransactionId(),
            event.getUsedAvailableAmount(),
            event.getWalletId(),
            event.getAmount()
        )));
  }

  @EndSaga
  @SagaEventHandler(associationProperty = "transactionId")
  public void handle(TransactionSetFinalStatusEvent event) {
    log.info(String.format("End of withdraw saga. Transaction status %s", event.getStatus()));
  }

  @SagaEventHandler(associationProperty = "transactionId")
  public void handle(CanceledWithdrawFromWalletEvent event) {
    commandGateway.sendAndWait(
        new SetTransactionFinalStatusCommand(event.getTransactionId(), TransactionStatus.FAILURE));
  }

  @SagaEventHandler(associationProperty = "transactionId")
  public void handle(CanceledSubtractionFromLoanLimitEvent event) {
    commandGateway.sendAndWait(new CancelWithdrawFromWalletCommand(
        event.getWalletId(),
        event.getTransactionId(),
        event.getAmount(),
        event.getUsedLoanLimitAmount()
    ));
  }
}
