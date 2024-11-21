package com.nerdysoft.axon.aggregate;

import com.nerdysoft.axon.command.loanlimit.CancelSubtractionFromLoanLimitCommand;
import com.nerdysoft.axon.command.loanlimit.RepayLoanLimitCommand;
import com.nerdysoft.axon.command.loanlimit.SubtractFromLoanLimitCommand;
import com.nerdysoft.axon.command.loanlimit.TakeLoanLimitCommand;
import com.nerdysoft.axon.event.loanlimit.CanceledSubtractionFromLoanLimitEvent;
import com.nerdysoft.axon.event.loanlimit.RepayLoanLimitEvent;
import com.nerdysoft.axon.event.loanlimit.SubtractedFromLoanLimitEvent;
import com.nerdysoft.axon.event.loanlimit.TakeLoanLimitEvent;
import com.nerdysoft.entity.loanlimit.LoanLimit;
import com.nerdysoft.service.loanlimit.LoanLimitService;
import java.util.UUID;
import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;

@Aggregate
@NoArgsConstructor
public class LoanLimitAggregate {
    @AggregateIdentifier
    private UUID id;

    @CommandHandler
    public LoanLimitAggregate(TakeLoanLimitCommand command, LoanLimitService loanLimitService) {
        LoanLimit loanLimit = loanLimitService.getLoanLimit(command.getAccountId(), command.getAccountEmail(), command.getCurrency());

        TakeLoanLimitEvent takeLoanLimitEvent = new TakeLoanLimitEvent();
        BeanUtils.copyProperties(loanLimit, takeLoanLimitEvent);

        AggregateLifecycle.apply(takeLoanLimitEvent);
    }

    @EventSourcingHandler
    public void on(TakeLoanLimitEvent event) {
        this.id = event.getId();
    }

    @CommandHandler
    public void handle(SubtractFromLoanLimitCommand command, LoanLimitService loanLimitService) {
        LoanLimit loanLimit = loanLimitService.subtractAvailableLoanLimitAmount(command.getLoanLimitId(), command.getUsedAvailableAmount());
        AggregateLifecycle.apply(new SubtractedFromLoanLimitEvent(command.getTransactionId(), loanLimit.getId(),
            command.getUsedAvailableAmount(), loanLimit.getAvailableAmount(), command.getWalletId(), command.getAmount()));
    }

    @CommandHandler
    public void handle(RepayLoanLimitCommand command) {
        RepayLoanLimitEvent repayLoanLimitEvent = new RepayLoanLimitEvent(command.getAccountId());
        AggregateLifecycle.apply(repayLoanLimitEvent);
    }

    @CommandHandler
    public void handle(CancelSubtractionFromLoanLimitCommand command, LoanLimitService loanLimitService) {
      LoanLimit loanLimit = loanLimitService.cancelSubtractionFromLoanLimit(command);

      AggregateLifecycle.apply(new CanceledSubtractionFromLoanLimitEvent(loanLimit.getId(), command.getTransactionId(),
          command.getWalletId(), command.getAmount(), command.getUsedAvailableAmount()));
    }
}
