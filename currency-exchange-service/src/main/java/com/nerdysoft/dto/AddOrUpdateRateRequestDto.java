package com.nerdysoft.dto;

import java.math.BigDecimal;

public record AddOrUpdateRateRequestDto(
        String fromCurrency,
        String toCurrency,
        BigDecimal exchangeRate
) {
}
