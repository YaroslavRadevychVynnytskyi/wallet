package com.nerdysoft.service.loanlimit.strategy;

import com.nerdysoft.service.loanlimit.strategy.handlers.LoanLimitHandler;
import java.math.BigDecimal;

public interface LoanLimitStrategy {
    LoanLimitHandler get(BigDecimal maxAmountForMonth, BigDecimal turnoverForMonth);
}
