package com.nerdysoft.service.strategy.impl;

import com.nerdysoft.service.strategy.CommissionStrategy;
import com.nerdysoft.service.strategy.Range;
import com.nerdysoft.service.strategy.handler.CommissionHandler;
import java.math.BigDecimal;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class CommissionStrategyImpl implements CommissionStrategy {
    private final Map<Range, CommissionHandler> commissionHandlerMap;

    public CommissionStrategyImpl(CommissionHandler lowAmountCommissionHandler,
                                  CommissionHandler mediumAmountCommissionHandler,
                                  CommissionHandler highAmountCommissionHandler) {
        commissionHandlerMap = Map.of(
                new Range(BigDecimal.ZERO, BigDecimal.valueOf(100)), lowAmountCommissionHandler,
                new Range(BigDecimal.valueOf(100), BigDecimal.valueOf(1000)), mediumAmountCommissionHandler,
                new Range(BigDecimal.valueOf(1000), null), highAmountCommissionHandler
        );
    }

    @Override
    public CommissionHandler get(BigDecimal amount) {
        return commissionHandlerMap.entrySet().stream()
                .filter(e -> e.getKey().isWithinRange(amount))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No commission handler found for amount: " + amount));
    }
}
