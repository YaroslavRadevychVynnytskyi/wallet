package com.nerdysoft.dto.request;

import java.math.BigDecimal;

public record AddOrUpdateRateRequestDto(
        String fromCurrency,
        String toCurrency,
        BigDecimal exchangeRate
) {
}
