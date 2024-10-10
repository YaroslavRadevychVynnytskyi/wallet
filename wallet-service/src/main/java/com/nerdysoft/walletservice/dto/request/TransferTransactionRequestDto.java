package com.nerdysoft.walletservice.dto.request;

import com.nerdysoft.walletservice.model.enums.Currency;
import java.math.BigDecimal;
import java.util.UUID;

public interface TransferTransactionRequestDto {
    Currency getCurrency();
    BigDecimal getAmount();
    UUID getToWalletId();
}
