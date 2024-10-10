package com.nerdysoft.service.loanlimit.strategy.handlers;

import java.math.BigDecimal;
import org.springframework.stereotype.Component;

@Component
public class HighLoanLimitHandler implements LoanLimitHandler {
    @Override
    public BigDecimal getLoanLimit() {
        return BigDecimal.valueOf(1000);
    }
}
