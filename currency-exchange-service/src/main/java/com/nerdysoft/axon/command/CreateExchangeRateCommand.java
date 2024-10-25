package com.nerdysoft.axon.command;

import java.math.BigDecimal;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CreateExchangeRateCommand {
  private final String baseCode;

  private final Map<String, BigDecimal> conversionRates;
}
