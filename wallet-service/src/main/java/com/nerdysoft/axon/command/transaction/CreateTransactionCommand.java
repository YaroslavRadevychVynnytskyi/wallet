package com.nerdysoft.axon.command.transaction;

import com.nerdysoft.model.enums.Currency;
import com.nerdysoft.model.enums.TransactionStatus;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CreateTransactionCommand {
  private final UUID walletId;
  private final UUID toWalletId;
  private final BigDecimal amount;
  private final Currency currency;
  private final TransactionStatus status;
  private final BigDecimal walletBalance;
}
