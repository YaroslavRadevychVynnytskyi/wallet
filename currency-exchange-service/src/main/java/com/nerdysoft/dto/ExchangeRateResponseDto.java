package com.nerdysoft.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ExchangeRateResponseDto(
        String fromCurrency,
        String toCurrency,
        BigDecimal exchangeRate,
        LocalDateTime timestamp
) {
}
