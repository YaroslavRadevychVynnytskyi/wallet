package com.nerdysoft.dto.feign;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ConvertAmountResponseDto(
        String fromCurrency,
        String toCurrency,
        BigDecimal originalAmount,
        BigDecimal convertedAmount,
        BigDecimal exchangeRate,
        LocalDateTime timestamp
) {
}
