package com.nerdysoft.walletservice.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Currency {
  USD("USD"),
  EURO("EURO"),
  UAH("UAH");

  private final String code;
}
