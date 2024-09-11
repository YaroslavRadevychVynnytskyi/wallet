package com.nerdysoft.dto.generic;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.Map;

public record ConversionRatesDto(
        @JsonProperty("conversion_rates")
        Map<String, BigDecimal> conversionRates
) {
}
