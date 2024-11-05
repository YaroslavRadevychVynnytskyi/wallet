package com.nerdysoft.dto.request;

import com.nerdysoft.model.enums.Currency;
import java.math.BigDecimal;
import java.util.UUID;

public record TransferRequestDto(UUID toWalletId, BigDecimal amount, Currency currency) implements
    WalletOperationRequestDto {
  @Override
  public Currency getCurrency() {
    return currency();
  }

  @Override
  public BigDecimal getAmount() {
    return amount();
  }

  @Override
  public UUID getToWalletId() {
    return toWalletId();
  }
}
