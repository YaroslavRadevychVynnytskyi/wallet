package com.nerdysoft.dto.request;

public record ExchangeRateRequestDto(
        String fromCurrency,
        String toCurrency
) {
}
