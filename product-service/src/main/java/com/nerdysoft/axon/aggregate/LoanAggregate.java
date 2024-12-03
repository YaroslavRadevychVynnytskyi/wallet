package com.nerdysoft.axon.aggregate;

import com.nerdysoft.axon.command.loan.ApplyLoanCommand;
import com.nerdysoft.axon.command.loan.DeleteLoanCommand;
import com.nerdysoft.axon.command.loan.HandleRejectedLoanCommand;
import com.nerdysoft.axon.command.loan.RepayLoanCommand;
import com.nerdysoft.axon.event.loan.ApplyLoanEvent;
import com.nerdysoft.axon.event.loan.DeleteLoanEvent;
import com.nerdysoft.axon.event.loan.RejectedLoanEvent;
import com.nerdysoft.axon.event.loan.RepayLoanEvent;
import com.nerdysoft.entity.loan.Loan;
import com.nerdysoft.model.enums.ApprovalStatus;
import com.nerdysoft.model.enums.Currency;
import com.nerdysoft.model.enums.PaymentType;
import com.nerdysoft.model.enums.RepaymentStatus;
import com.nerdysoft.service.loan.LoanService;
import java.math.BigDecimal;
import java.time.LocalDate;
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
public class LoanAggregate {
    @AggregateIdentifier
    private UUID id;
    private UUID accountId;
    private String accountEmail;
    private UUID walletId;
    private Currency walletCurrency;
    private ApprovalStatus approvalStatus;
    private PaymentType paymentType;
    private BigDecimal usdLoanAmount;
    private BigDecimal walletCurrencyLoanAmount;
    private BigDecimal interestRate;
    private BigDecimal usdRepaymentAmount;
    private BigDecimal walletCurrencyRepaymentAmount;
    private BigDecimal usdRemainingRepaymentAmount;
    private BigDecimal repaymentTermInMonths;
    private BigDecimal usdMonthlyRepaymentAmount;
    private BigDecimal totalPaymentsMade;
    private RepaymentStatus repaymentStatus;
    private LocalDateTime timestamp;
    private LocalDate nextPayment;
    private LocalDateTime dueDate;

    @CommandHandler
    public LoanAggregate(ApplyLoanCommand applyLoanCommand, LoanService loanService) {
        Loan loan = loanService.applyForLoan(
                applyLoanCommand.getAccountId(),
                applyLoanCommand.getAccountEmail(),
                applyLoanCommand.getRequestedAmount(),
                applyLoanCommand.getCurrency(),
                applyLoanCommand.getPaymentType()
        );

        ApplyLoanEvent applyLoanEvent = new ApplyLoanEvent();
        BeanUtils.copyProperties(loan, applyLoanEvent);

        AggregateLifecycle.apply(applyLoanEvent);
    }

    @EventSourcingHandler
    public void on(ApplyLoanEvent applyLoanEvent) {
        this.id = applyLoanEvent.getId();
        this.accountId = applyLoanEvent.getAccountId();
        this.accountEmail = applyLoanEvent.getAccountEmail();
        this.walletId = applyLoanEvent.getWalletId();
        this.walletCurrency = applyLoanEvent.getWalletCurrency();
        this.approvalStatus = applyLoanEvent.getApprovalStatus();
        this.paymentType = applyLoanEvent.getPaymentType();
        this.usdLoanAmount = applyLoanEvent.getUsdLoanAmount();
        this.walletCurrencyLoanAmount = applyLoanEvent.getWalletCurrencyLoanAmount();
        this.interestRate = applyLoanEvent.getInterestRate();
        this.usdRepaymentAmount = applyLoanEvent.getUsdRepaymentAmount();
        this.walletCurrencyRepaymentAmount = applyLoanEvent.getWalletCurrencyRepaymentAmount();
        this.usdRemainingRepaymentAmount = applyLoanEvent.getUsdRemainingRepaymentAmount();
        this.repaymentTermInMonths = applyLoanEvent.getRepaymentTermInMonths();
        this.usdMonthlyRepaymentAmount = applyLoanEvent.getUsdMonthlyRepaymentAmount();
        this.totalPaymentsMade = applyLoanEvent.getTotalPaymentsMade();
        this.repaymentStatus = applyLoanEvent.getRepaymentStatus();
        this.timestamp = applyLoanEvent.getTimestamp();
        this.nextPayment = applyLoanEvent.getNextPayment();
        this.dueDate = applyLoanEvent.getDueDate();
    }

    @CommandHandler
    public void handle(RepayLoanCommand repayLoanCommand) {
        RepayLoanEvent repayLoanEvent = new RepayLoanEvent(repayLoanCommand.getAccountId());

        AggregateLifecycle.apply(repayLoanEvent);
    }

    @EventSourcingHandler
    public void on(RepayLoanEvent repayLoanEvent) {
        this.totalPaymentsMade = totalPaymentsMade.add(BigDecimal.ONE);
        this.nextPayment = nextPayment.plusMonths(1);

        if (totalPaymentsMade.compareTo(repaymentTermInMonths) == 0) {
            this.repaymentStatus = RepaymentStatus.COMPLETED;
        }
    }

    @CommandHandler
    public void handle(HandleRejectedLoanCommand handleRejectedLoanCommand) {
        RejectedLoanEvent rejectedLoanEvent = new RejectedLoanEvent(handleRejectedLoanCommand.getId());

        AggregateLifecycle.apply(rejectedLoanEvent);
    }

    @CommandHandler
    public void handle(DeleteLoanCommand deleteLoanCommand) {
        DeleteLoanEvent deleteLoanEvent = new DeleteLoanEvent(deleteLoanCommand.getId());

        AggregateLifecycle.apply(deleteLoanEvent);
    }

    @EventSourcingHandler
    public void on(DeleteLoanEvent deleteLoanEvent) {
        AggregateLifecycle.markDeleted();
    }
}
