package com.nerdysoft.dto.request;

import com.nerdysoft.model.enums.Currency;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
public abstract class WalletOperationRequestDto {
  private BigDecimal amount;

  private Currency currency;
}
