package com.nerdysoft.dto.request;

import java.math.BigDecimal;
import java.util.Map;

public record AddOrUpdateRateRequestDto(
    String baseCode,
    Map<String, BigDecimal> conversionRates
) {
}
