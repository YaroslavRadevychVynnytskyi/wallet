package com.nerdysoft.axon.event.exchangerate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRateCreatedEvent {
  private String baseCode;

  private Map<String, BigDecimal> conversionRates;

  private LocalDateTime timestamp;
}
