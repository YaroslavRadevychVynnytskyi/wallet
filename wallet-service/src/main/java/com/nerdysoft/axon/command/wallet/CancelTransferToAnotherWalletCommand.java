package com.nerdysoft.axon.command.wallet;

import com.nerdysoft.model.enums.Currency;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CancelTransferToAnotherWalletCommand {
  @TargetAggregateIdentifier
  private UUID fromWalletId;

  private UUID transactionId;

  private BigDecimal cleanAmount;

  private Currency operationCurrency;

  private BigDecimal commission;
}
