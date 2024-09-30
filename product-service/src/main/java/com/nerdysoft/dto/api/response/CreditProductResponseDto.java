package com.nerdysoft.dto.api.response;

import java.math.BigDecimal;
import java.util.UUID;

public record CreditProductResponseDto(
        UUID id,
        String name,
        BigDecimal minAmount,
        BigDecimal maxAmount,
        BigDecimal interestRate,
        Integer termInMonths,
        BigDecimal penaltyRate
) {
}
