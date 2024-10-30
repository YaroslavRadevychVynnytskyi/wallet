package com.nerdysoft.dto.response;

import com.nerdysoft.entity.ExchangeRate;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRateResponseDto {
    private String fromCurrency;
    private String toCurrency;
    private BigDecimal exchangeRate;
    private LocalDateTime timestamp;

    public ExchangeRateResponseDto(ExchangeRate rate, String toCurrency) {
        fromCurrency = rate.getBaseCode();
        this.toCurrency = toCurrency;
        exchangeRate = rate.getConversionRates().get(toCurrency);
        timestamp = rate.getTimestamp();
    }
}
