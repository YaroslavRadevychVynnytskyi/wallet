package com.nerdysoft.service.loan.strategy;

import com.nerdysoft.service.loan.strategy.handlers.LoanHandler;
import java.math.BigDecimal;

public interface LoanStrategy {
    LoanHandler get(BigDecimal maxBalanceForLastMonth, BigDecimal turnoverForLastMonth);
}
