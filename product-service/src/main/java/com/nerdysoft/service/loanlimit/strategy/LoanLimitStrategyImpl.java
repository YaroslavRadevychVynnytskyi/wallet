package com.nerdysoft.service.loanlimit.strategy;

import com.nerdysoft.service.loanlimit.strategy.handlers.LoanLimitHandler;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class LoanLimitStrategyImpl implements LoanLimitStrategy {
    private final Map<BalanceCriteria, LoanLimitHandler> loanLimitHandlerMap;

    public LoanLimitStrategyImpl(LoanLimitHandler lowLoanLimitHandler,
                                 LoanLimitHandler mediumLoanLimitHandler,
                                 LoanLimitHandler highLoanLimitHandler,
                                 LoanLimitHandler defaultLoanLimitHandler) {
        loanLimitHandlerMap = Map.of(
                new BalanceCriteria(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO), defaultLoanLimitHandler,
                new BalanceCriteria(BigDecimal.valueOf(500), BigDecimal.valueOf(1000), BigDecimal.valueOf(1000), BigDecimal.valueOf(2000)), lowLoanLimitHandler,
                new BalanceCriteria(BigDecimal.valueOf(1000), BigDecimal.valueOf(2000), BigDecimal.valueOf(2000), BigDecimal.valueOf(5000)), mediumLoanLimitHandler,
                new BalanceCriteria(BigDecimal.valueOf(2000), null, BigDecimal.valueOf(5000), null), highLoanLimitHandler);
    }

    @Override
    public LoanLimitHandler get(BigDecimal maxAmountForMonth, BigDecimal turnoverForMonth) {
        return loanLimitHandlerMap.entrySet().stream()
                .filter(e -> e.getKey().isMatchingCriteria(maxAmountForMonth, turnoverForMonth))
                .max(Comparator.comparing(e -> calculateMaxValue(e.getKey(), maxAmountForMonth, turnoverForMonth)))
                .map(Map.Entry::getValue)
                .orElse(loanLimitHandlerMap.get(new BalanceCriteria(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO)));
    }

    private BigDecimal calculateMaxValue(BalanceCriteria criteria, BigDecimal maxAmountForMonth, BigDecimal turnoverForMonth) {
        BigDecimal maxForBalance = criteria.isWithinBounds(maxAmountForMonth, criteria.getMaxAmountLowerBound(), criteria.getMaxAmountUpperBound()) ? maxAmountForMonth : BigDecimal.ZERO;
        BigDecimal maxForTurnover = criteria.isWithinBounds(turnoverForMonth, criteria.getTurnoverLowerBound(), criteria.getTurnoverUpperBound()) ? turnoverForMonth : BigDecimal.ZERO;

        return maxForBalance.max(maxForTurnover);
    }
}
