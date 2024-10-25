package com.nerdysoft.axon.query;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ConvertQuery {
  private String fromCurrency;
  private String toCurrency;
  private BigDecimal amount;
}
