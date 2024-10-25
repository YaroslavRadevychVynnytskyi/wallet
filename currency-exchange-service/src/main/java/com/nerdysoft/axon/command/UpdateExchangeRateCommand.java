package com.nerdysoft.axon.command;

import java.math.BigDecimal;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Getter
@RequiredArgsConstructor
public class UpdateExchangeRateCommand {
  @TargetAggregateIdentifier
  private final String baseCode;

  private final Map<String, BigDecimal> conversionRates;
}
