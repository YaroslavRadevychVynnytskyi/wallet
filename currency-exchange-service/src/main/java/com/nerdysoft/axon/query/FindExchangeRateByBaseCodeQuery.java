package com.nerdysoft.axon.query;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FindExchangeRateByBaseCodeQuery {
  private String baseCode;
}
