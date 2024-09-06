package com.nerdysoft.walletservice.dto.request;

import com.nerdysoft.walletservice.model.enums.Currency;

public record TransactionRequestDto(Double amount, Currency currency) {}
