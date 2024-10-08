package com.nerdysoft.service.strategy;

import com.nerdysoft.service.strategy.handler.CommissionHandler;
import java.math.BigDecimal;
import java.util.List;

public interface CommissionStrategy {
    List<CommissionHandler> get(BigDecimal amount, boolean isLoanUsed);
}
