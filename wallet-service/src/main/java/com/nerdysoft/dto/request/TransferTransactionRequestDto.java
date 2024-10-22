package com.nerdysoft.dto.request;

import com.nerdysoft.entity.enums.Currency;
import java.math.BigDecimal;
import java.util.UUID;

public interface TransferTransactionRequestDto {
    Currency getCurrency();
    BigDecimal getAmount();
    UUID getToWalletId();
}
