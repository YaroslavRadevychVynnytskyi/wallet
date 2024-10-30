package com.nerdysoft.service.loanlimit.strategy;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BalanceCriteria {
    private BigDecimal maxAmountLowerBound;
    private BigDecimal maxAmountUpperBound;

    private BigDecimal turnoverLowerBound;
    private BigDecimal turnoverUpperBound;

    public boolean isMatchingCriteria(BigDecimal maxAmountForMonth, BigDecimal turnoverForMonth) {
        return isWithinBounds(maxAmountForMonth, maxAmountLowerBound, maxAmountUpperBound)
                || isWithinBounds(turnoverForMonth, turnoverLowerBound, turnoverUpperBound);
    }

    public boolean isWithinBounds(BigDecimal value, BigDecimal lowerBound, BigDecimal upperBound) {
        boolean meetsLowerBound = value.compareTo(lowerBound) >= 0;
        boolean meetsUpperBound = upperBound == null || value.compareTo(upperBound) < 0;

        return meetsLowerBound && meetsUpperBound;
    }
}
