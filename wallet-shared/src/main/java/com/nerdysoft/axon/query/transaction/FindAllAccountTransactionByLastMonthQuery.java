package com.nerdysoft.axon.query.transaction;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FindAllAccountTransactionByLastMonthQuery {
  private UUID accountId;
}
