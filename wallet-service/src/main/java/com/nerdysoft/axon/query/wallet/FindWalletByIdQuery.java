package com.nerdysoft.axon.query.wallet;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FindWalletByIdQuery {
  private UUID id;
}
