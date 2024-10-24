package com.nerdysoft.walletservice.axon.command.wallet;

import com.nerdysoft.model.enums.Currency;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Getter
@RequiredArgsConstructor
public class WithdrawFromWalletCommand {
  @TargetAggregateIdentifier
  private final UUID walletId;

  private final BigDecimal amount;

  private final Currency currency;
}
