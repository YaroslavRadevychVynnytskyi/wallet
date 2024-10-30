package com.nerdysoft.axon.handler.loan;

import com.nerdysoft.axon.event.loan.RepayLoanEvent;
import com.nerdysoft.service.loan.LoanService;
import lombok.RequiredArgsConstructor;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LoanEventsHandler {
    private final LoanService loanService;

    @EventHandler
    public void on(RepayLoanEvent event) {
        loanService.manualLoanRepay(event.getAccountId());
    }
}
