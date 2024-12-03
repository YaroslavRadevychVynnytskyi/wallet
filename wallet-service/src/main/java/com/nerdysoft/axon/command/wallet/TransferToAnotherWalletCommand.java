package com.nerdysoft.axon.command.wallet;

import com.nerdysoft.model.enums.Currency;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TransferToAnotherWalletCommand {
  @TargetAggregateIdentifier
  private UUID fromWalletId;

  private UUID toWalletId;

  private BigDecimal amount;

  private Currency currency;
}
