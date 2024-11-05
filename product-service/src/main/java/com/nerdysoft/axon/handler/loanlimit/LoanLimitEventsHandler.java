package com.nerdysoft.axon.handler.loanlimit;

import com.nerdysoft.axon.event.loanlimit.RepayLoanLimitEvent;
import com.nerdysoft.service.loanlimit.LoanLimitService;
import lombok.RequiredArgsConstructor;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LoanLimitEventsHandler {
    private final LoanLimitService loanLimitService;

    @EventHandler
    public void on(RepayLoanLimitEvent event) {
        loanLimitService.repayLoanLimit(event.getAccountId());
    }
}

