package com.nerdysoft.service.loanlimit.strategy.handlers;

import java.math.BigDecimal;

public interface LoanLimitHandler {
    BigDecimal getLoanLimit();
}
