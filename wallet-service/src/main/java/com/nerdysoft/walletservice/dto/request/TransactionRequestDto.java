package com.nerdysoft.walletservice.dto.request;

import com.nerdysoft.walletservice.model.enums.Currency;
import java.math.BigDecimal;

public record TransactionRequestDto(BigDecimal amount, Currency currency) {}
