package com.nerdysoft.axon.event;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AccountCreatedEvent {
  private UUID accountId;

  private String email;
}
