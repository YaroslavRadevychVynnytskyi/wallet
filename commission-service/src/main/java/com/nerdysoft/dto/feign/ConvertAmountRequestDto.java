package com.nerdysoft.dto.feign;

import java.math.BigDecimal;

public record ConvertAmountRequestDto(
        String fromCurrency,
        String toCurrency,
        BigDecimal amount
) {
}
