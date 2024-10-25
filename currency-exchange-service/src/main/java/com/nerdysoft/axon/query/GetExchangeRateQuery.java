package com.nerdysoft.axon.query;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GetExchangeRateQuery {
  private String fromCurrency;
  private String toCurrency;
}
