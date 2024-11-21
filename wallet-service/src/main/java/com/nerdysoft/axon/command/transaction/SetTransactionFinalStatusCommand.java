package com.nerdysoft.axon.command.transaction;

import com.nerdysoft.model.enums.TransactionStatus;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SetTransactionFinalStatusCommand {
  @TargetAggregateIdentifier
  private UUID transactionId;

  private TransactionStatus status;
}
