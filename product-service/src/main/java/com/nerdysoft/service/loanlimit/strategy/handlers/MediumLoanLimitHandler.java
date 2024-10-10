package com.nerdysoft.service.loanlimit.strategy.handlers;

import java.math.BigDecimal;
import org.springframework.stereotype.Component;

@Component
public class MediumLoanLimitHandler implements LoanLimitHandler {
    @Override
    public BigDecimal getLoanLimit() {
        return BigDecimal.valueOf(500);
    }
}
