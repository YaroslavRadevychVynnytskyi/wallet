package com.nerdysoft.axon.aggregate;

import com.nerdysoft.axon.command.loanlimit.RepayLoanLimitCommand;
import com.nerdysoft.axon.command.loanlimit.TakeLoanLimitCommand;
import com.nerdysoft.axon.event.loanlimit.RepayLoanLimitEvent;
import com.nerdysoft.axon.event.loanlimit.TakeLoanLimitEvent;
import com.nerdysoft.entity.loanlimit.LoanLimit;
import com.nerdysoft.model.enums.Currency;
import com.nerdysoft.service.loanlimit.LoanLimitService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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
    private UUID accountId;
    private String accountEmail;
    private UUID walletId;
    private BigDecimal availableAmount;
    private BigDecimal initialAmount;
    private boolean isRepaid;
    private Currency currency;
    private LocalDateTime timestamp = LocalDateTime.now();
    private LocalDateTime dueDate = LocalDateTime.now().plusMonths(1).withDayOfMonth(25);

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
        this.accountId = event.getAccountId();
        this.accountEmail = event.getAccountEmail();
        this.walletId = event.getWalletId();
        this.availableAmount = event.getAvailableAmount();
        this.initialAmount = event.getInitialAmount();
        this.isRepaid = event.isRepaid();
        this.currency = event.getCurrency();
        this.timestamp = event.getTimestamp();
        this.dueDate = event.getDueDate();
    }

    @CommandHandler
    public void handle(RepayLoanLimitCommand command) {
        RepayLoanLimitEvent repayLoanLimitEvent = new RepayLoanLimitEvent(command.getAccountId());
        AggregateLifecycle.apply(repayLoanLimitEvent);
    }

    @EventSourcingHandler
    public void on(RepayLoanLimitEvent event) {
        isRepaid = true;
        availableAmount = BigDecimal.ZERO;
    }
}
