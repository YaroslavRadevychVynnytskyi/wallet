package com.nerdysoft.axon.command.wallet;

import com.nerdysoft.model.enums.Currency;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class DepositToWalletCommand {
  @TargetAggregateIdentifier
  private UUID walletId;

  private BigDecimal amount;

  private Currency currency;
}
