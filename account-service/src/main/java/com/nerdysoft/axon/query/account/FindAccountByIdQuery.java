package com.nerdysoft.axon.query.account;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FindAccountByIdQuery {
  private UUID accountId;
}
