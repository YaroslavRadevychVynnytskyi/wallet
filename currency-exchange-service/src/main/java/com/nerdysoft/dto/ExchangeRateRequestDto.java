package com.nerdysoft.dto;

public record ExchangeRateRequestDto(
        String fromCurrency,
        String toCurrency
) {
}
