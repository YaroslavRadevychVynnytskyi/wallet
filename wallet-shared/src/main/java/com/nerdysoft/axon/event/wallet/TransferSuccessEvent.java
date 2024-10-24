package com.nerdysoft.axon.event.wallet;

import com.nerdysoft.model.enums.Currency;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TransferSuccessEvent {
  private UUID fromWalletId;

  private UUID toWalletId;

  private BigDecimal fromWalletBalance;

  private BigDecimal toWalletBalance;

  private BigDecimal amount;

  private Currency currency;

  private UUID transactionId;
}
