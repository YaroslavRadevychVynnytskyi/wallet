package com.nerdysoft.walletservice.dto.request;

import com.nerdysoft.model.enums.Currency;
import java.math.BigDecimal;
import java.util.UUID;

public interface WalletOperationRequestDto {
  Currency getCurrency();

  BigDecimal getAmount();

  UUID getToWalletId();
}
