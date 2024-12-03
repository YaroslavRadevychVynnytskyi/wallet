package com.nerdysoft.axon.aggregate;

import com.nerdysoft.axon.command.loanlimit.CancelUpdateLoanLimitAfterTransferCommand;
import com.nerdysoft.axon.command.loanlimit.RepayLoanLimitCommand;
import com.nerdysoft.axon.command.loanlimit.TakeLoanLimitCommand;
import com.nerdysoft.axon.command.loanlimit.UpdateLoanLimitAfterTransferCommand;
import com.nerdysoft.axon.command.loanlimit.UpdateLoanLimitAfterWithdrawCommand;
import com.nerdysoft.axon.event.loanlimit.CanceledUpdateLoanLimitAfterTransferEvent;
import com.nerdysoft.axon.event.loanlimit.LoanLimitRepaidEvent;
import com.nerdysoft.axon.event.loanlimit.LoanLimitTookEvent;
import com.nerdysoft.axon.event.loanlimit.UpdatedLoanLimitAfterTransferEvent;
import com.nerdysoft.axon.event.loanlimit.UpdatedLoanLimitAfterWithdrawEvent;
import com.nerdysoft.entity.loanlimit.LoanLimit;
import com.nerdysoft.service.loanlimit.LoanLimitService;
import java.util.UUID;
import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

@Aggregate
@NoArgsConstructor
public class LoanLimitAggregate {
  @AggregateIdentifier
  private UUID id;

  @CommandHandler
  public LoanLimitAggregate(TakeLoanLimitCommand command, LoanLimitService loanLimitService) {
    LoanLimit loanLimit = loanLimitService.getLoanLimit(command.getAccountId(), command.getEmail());

    LoanLimitTookEvent loanLimitTookEvent = LoanLimitTookEvent.builder()
        .id(loanLimit.getId())
        .accountId(loanLimit.getAccountId())
        .email(loanLimit.getEmail())
        .availableAmount(loanLimit.getAvailableAmount())
        .initialAmount(loanLimit.getInitialAmount())
        .repaid(loanLimit.isRepaid())
        .dueDate(loanLimit.getDueDate())
        .build();

    AggregateLifecycle.apply(loanLimitTookEvent);
  }

  @EventSourcingHandler
  public void on(LoanLimitTookEvent event) {
    this.id = event.getId();
  }

  @CommandHandler
  public void handle(UpdateLoanLimitAfterWithdrawCommand command,
      LoanLimitService loanLimitService) {
    LoanLimit loanLimit = loanLimitService.subtractAvailableLoanLimitAmount(
        command.getLoanLimitId(), command.getUsedAvailableAmount());
    AggregateLifecycle.apply(
        new UpdatedLoanLimitAfterWithdrawEvent(command.getTransactionId(), loanLimit.getId(),
            command.getUsedAvailableAmount(), loanLimit.getAvailableAmount(), command.getWalletId(),
            command.getAmount()));
  }

  @CommandHandler
  public void handle(UpdateLoanLimitAfterTransferCommand command,
      LoanLimitService loanLimitService) {
    LoanLimit loanLimit = loanLimitService.subtractAvailableLoanLimitAmount(
        command.getLoanLimitId(), command.getUsedLoanLimitAmount());

    UpdatedLoanLimitAfterTransferEvent event = UpdatedLoanLimitAfterTransferEvent.builder()
        .transactionId(command.getTransactionId())
        .loanLimitId(loanLimit.getId())
        .accountId(command.getAccountId())
        .fromWalletId(command.getFromWalletId())
        .toWalletId(command.getToWalletId())
        .cleanAmount(command.getCleanAmount())
        .operationCurrency(command.getOperationCurrency())
        .walletCurrency(command.getWalletCurrency())
        .usedLoanLimit(command.isUsedLoanLimit())
        .usedLoanLimitAmount(command.getUsedLoanLimitAmount())
        .commission(command.getCommission())
        .build();

    AggregateLifecycle.apply(event);
  }

  @CommandHandler
  public void handle(CancelUpdateLoanLimitAfterTransferCommand command,
      LoanLimitService loanLimitService) {
    LoanLimit loanLimit = loanLimitService.cancelUpdateLoanLimit(command.getLoanLimitId(),
        command.getUsedLoanLimitAmount());

    CanceledUpdateLoanLimitAfterTransferEvent event = CanceledUpdateLoanLimitAfterTransferEvent.builder()
        .loanLimitId(loanLimit.getId())
        .transactionId(command.getTransactionId())
        .accountId(command.getAccountId())
        .fromWalletId(command.getFromWalletId())
        .toWalletId(command.getToWalletId())
        .cleanAmount(command.getCleanAmount())
        .operationCurrency(command.getOperationCurrency())
        .walletCurrency(command.getWalletCurrency())
        .usedLoanLimit(command.isUsedLoanLimit())
        .usedLoanLimitAmount(command.getUsedLoanLimitAmount())
        .commission(command.getCommission())
        .build();

    AggregateLifecycle.apply(event);
  }

  @CommandHandler
  public void handle(RepayLoanLimitCommand command, LoanLimitService loanLimitService) {
    LoanLimit loanLimit = loanLimitService.repayLoanLimit(command.getAccountId());

    AggregateLifecycle.apply(new LoanLimitRepaidEvent(loanLimit.getId(), loanLimit.getAccountId()));
  }
}
