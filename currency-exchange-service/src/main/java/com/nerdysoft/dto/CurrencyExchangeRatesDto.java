package com.nerdysoft.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

public record CurrencyExchangeRatesDto(
        String result,
        String documentation,
        String termsOfUse,
        long timeLastUpdateUnix,
        String timeLastUpdateUtc,
        long timeNextUpdateUnix,
        String timeNextUpdateUtc,
        String baseCode,

        @JsonProperty("conversion_rates")
        Map<String, Double> conversionRates
) {
}
