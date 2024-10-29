package com.nerdysoft.service.loan.strategy;

import com.nerdysoft.service.loan.strategy.handlers.LoanHandler;
import com.nerdysoft.service.loanlimit.strategy.BalanceCriteria;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class LoanStrategyImpl implements LoanStrategy {
    private final Map<BalanceCriteria, LoanHandler> loanHandlerMap;

    public LoanStrategyImpl(LoanHandler lowLoanHandler,
                            LoanHandler mediumLoanHandler,
                            LoanHandler highLoanHandler) {
        loanHandlerMap = Map.of(
                new BalanceCriteria(BigDecimal.valueOf(500), BigDecimal.valueOf(1000), BigDecimal.valueOf(1000), BigDecimal.valueOf(2000)), lowLoanHandler,
                new BalanceCriteria(BigDecimal.valueOf(1000), BigDecimal.valueOf(2000), BigDecimal.valueOf(2000), BigDecimal.valueOf(5000)), mediumLoanHandler,
                new BalanceCriteria(BigDecimal.valueOf(2000), null, BigDecimal.valueOf(5000), null), highLoanHandler
        );
    }


    @Override
    public LoanHandler get(BigDecimal maxBalanceForLastMonth, BigDecimal turnoverForLastMonth) {
        return loanHandlerMap.entrySet().stream()
                .filter(e -> e.getKey().isMatchingCriteria(maxBalanceForLastMonth, turnoverForLastMonth))
                .max(Comparator.comparing(e -> calculateMaxValue(e.getKey(), maxBalanceForLastMonth, turnoverForLastMonth)))
                .map(Map.Entry::getValue)
                .orElseThrow();
    }

    private BigDecimal calculateMaxValue(BalanceCriteria criteria, BigDecimal maxAmountForMonth, BigDecimal turnoverForMonth) {
        BigDecimal maxForBalance = criteria.isWithinBounds(maxAmountForMonth, criteria.getMaxAmountLowerBound(), criteria.getMaxAmountUpperBound()) ? maxAmountForMonth : BigDecimal.ZERO;
        BigDecimal maxForTurnover = criteria.isWithinBounds(turnoverForMonth, criteria.getTurnoverLowerBound(), criteria.getTurnoverUpperBound()) ? turnoverForMonth : BigDecimal.ZERO;

        return maxForBalance.max(maxForTurnover);
    }
}
