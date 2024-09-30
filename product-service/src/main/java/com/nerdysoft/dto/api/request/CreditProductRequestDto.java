package com.nerdysoft.dto.api.request;

import java.math.BigDecimal;

public record CreditProductRequestDto(
        String name,
        BigDecimal minAmount,
        BigDecimal maxAmount,
        BigDecimal interestRate,
        Integer termInMonths,
        BigDecimal penaltyRate
) {
}

