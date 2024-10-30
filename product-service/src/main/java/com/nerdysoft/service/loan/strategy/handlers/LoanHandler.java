package com.nerdysoft.service.loan.strategy.handlers;

import java.math.BigDecimal;

public interface LoanHandler {
    LoanDetails getLoan(BigDecimal requestedAmount);
}
