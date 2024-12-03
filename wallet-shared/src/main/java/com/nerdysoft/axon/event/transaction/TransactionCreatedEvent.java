package com.nerdysoft.axon.event.transaction;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionCreatedEvent {
  private UUID transactionId;
}
