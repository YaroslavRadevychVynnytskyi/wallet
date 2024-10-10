package com.nerdysoft.dto.api.request;

import com.nerdysoft.dto.event.transaction.enums.TransactionType;
import com.nerdysoft.dto.feign.Currency;
import java.math.BigDecimal;

public record GenericTransactionRequestDto(
        BigDecimal amount,
        Currency walletCurrency,
        Currency currency,
        TransactionType transactionType
) {
}
