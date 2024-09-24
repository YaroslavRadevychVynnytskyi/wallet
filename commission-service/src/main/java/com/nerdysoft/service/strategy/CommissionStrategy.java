package com.nerdysoft.service.strategy;

import com.nerdysoft.service.strategy.handler.CommissionHandler;
import java.math.BigDecimal;

public interface CommissionStrategy {
    CommissionHandler get(BigDecimal amount);
}
