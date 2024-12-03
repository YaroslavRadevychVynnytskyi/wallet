package com.nerdysoft.axon.event.transaction;

import com.nerdysoft.model.enums.TransactionStatus;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionSetFinalStatusEvent {
  private UUID transactionId;

  private TransactionStatus status;
}
