package com.nerdysoft.walletservice.dto.request;

import java.math.BigDecimal;

public record ConvertAmountRequestDto(
    String fromCurrency,
    String toCurrency,
    BigDecimal amount
) {}
