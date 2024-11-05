package com.nerdysoft.axon.event.account;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AccountUpdatedEvent {
  private UUID accountId;

  private String fullName;

  private String email;
}
