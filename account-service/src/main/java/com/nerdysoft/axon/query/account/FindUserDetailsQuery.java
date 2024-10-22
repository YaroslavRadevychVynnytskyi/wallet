package com.nerdysoft.axon.query.account;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FindUserDetailsQuery {
  private String email;
}
