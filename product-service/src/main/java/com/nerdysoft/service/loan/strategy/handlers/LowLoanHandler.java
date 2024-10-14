package com.nerdysoft.service.loan.strategy.handlers;

import com.nerdysoft.entity.loan.enums.ApprovalStatus;
import java.math.BigDecimal;
import org.springframework.stereotype.Component;

@Component
public class LowLoanHandler implements LoanHandler {
    private static final BigDecimal REPAYMENT_TERM_IN_MONTHS = BigDecimal.valueOf(3);
    private static final BigDecimal INTEREST_RATE = BigDecimal.valueOf(1.1);
    private static final BigDecimal LOAN_BOUND = BigDecimal.valueOf(2_000);

    @Override
    public LoanDetails getLoan(BigDecimal requestedAmount) {
        if (requestedAmount.compareTo(LOAN_BOUND) < 0) {
            return new LoanDetails(REPAYMENT_TERM_IN_MONTHS, INTEREST_RATE.multiply(requestedAmount),
                    ApprovalStatus.APPROVED, INTEREST_RATE);
        }
        return new LoanDetails(BigDecimal.ZERO, BigDecimal.ZERO, ApprovalStatus.REJECTED, BigDecimal.ZERO);
    }
}
