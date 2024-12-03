package com.nerdysoft.axon.aggregate;

import com.nerdysoft.axon.command.commission.DeleteCommissionCommand;
import com.nerdysoft.axon.command.commission.SaveCommissionCommand;
import com.nerdysoft.axon.event.commission.CommissionDeletedEvent;
import com.nerdysoft.axon.event.commission.CommissionSavedEvent;
import com.nerdysoft.entity.Commission;
import com.nerdysoft.service.CommissionService;
import java.util.UUID;
import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

@Aggregate
@NoArgsConstructor
public class CommissionAggregate {
  @AggregateIdentifier
  private UUID commissionId;

  @CommandHandler
  public CommissionAggregate(SaveCommissionCommand command, CommissionService commissionService) {
    Commission commission = commissionService.saveCommission(command);

    CommissionSavedEvent event = CommissionSavedEvent.builder()
        .commissionId(commission.getCommissionId())
        .transactionId(command.getTransactionId())
        .loanLimitId(command.getLoanLimitId())
        .accountId(command.getAccountId())
        .fromWalletId(command.getFromWalletId())
        .toWalletId(command.getToWalletId())
        .cleanAmount(command.getCleanAmount())
        .operationCurrency(command.getOperationCurrency())
        .walletCurrency(command.getWalletCurrency())
        .usedLoanLimit(command.isUsedLoanLimit())
        .usedLoanLimitAmount(command.getUsedLoanLimitAmount())
        .commission(commission.getCommissionAmount())
        .build();

    AggregateLifecycle.apply(event);
  }

  @EventSourcingHandler
  public void on(CommissionSavedEvent event) {
    commissionId = event.getCommissionId();
  }

  @CommandHandler
  public void handle(DeleteCommissionCommand command, CommissionService commissionService) {
    commissionService.delete(command.getCommissionId());

    CommissionDeletedEvent event = CommissionDeletedEvent.builder()
        .transactionId(command.getTransactionId())
        .loanLimitId(command.getLoanLimitId())
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

  @EventSourcingHandler
  public void on(CommissionDeletedEvent event) {
    AggregateLifecycle.markDeleted();
  }
}
