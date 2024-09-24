package com.nerdysoft.service.strategy;

import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Range {
    private final BigDecimal lowerBound;
    private final BigDecimal upperBound;

    public boolean isWithinRange(BigDecimal value) {
        return (lowerBound == null || value.compareTo(lowerBound) > 0)
                && (upperBound == null || value.compareTo(upperBound) <= 0);
    }
}
