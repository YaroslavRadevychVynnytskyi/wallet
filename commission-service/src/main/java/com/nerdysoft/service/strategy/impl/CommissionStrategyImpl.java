package com.nerdysoft.service.strategy.impl;

import com.nerdysoft.service.strategy.CommissionStrategy;
import com.nerdysoft.service.strategy.Range;
import com.nerdysoft.service.strategy.handler.CommissionHandler;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class CommissionStrategyImpl implements CommissionStrategy {
    private final Map<Range, CommissionHandler> commissionHandlerMap;
    private final CommissionHandler loanCommissionHandler;

    public CommissionStrategyImpl(CommissionHandler lowAmountCommissionHandler,
                                  CommissionHandler mediumAmountCommissionHandler,
                                  CommissionHandler highAmountCommissionHandler,
                                  CommissionHandler loanCommissionHandler) {
        commissionHandlerMap = Map.of(
                new Range(BigDecimal.ZERO, BigDecimal.valueOf(100)), lowAmountCommissionHandler,
                new Range(BigDecimal.valueOf(100), BigDecimal.valueOf(1000)), mediumAmountCommissionHandler,
                new Range(BigDecimal.valueOf(1000), null), highAmountCommissionHandler
        );
        this.loanCommissionHandler = loanCommissionHandler;
    }

    @Override
    public List<CommissionHandler> get(BigDecimal amount, boolean isLoanUsed) {
        List<CommissionHandler> commissionHandlers = commissionHandlerMap.entrySet().stream()
                .filter(e -> e.getKey().isWithinRange(amount))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());

        if (isLoanUsed) {
            commissionHandlers.add(loanCommissionHandler);
        }

        return commissionHandlers;
    }
}
