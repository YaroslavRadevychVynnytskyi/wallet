package com.nerdysoft.axon.handler;

import com.nerdysoft.axon.query.account.FindAccountByEmailQuery;
import com.nerdysoft.axon.query.account.FindAccountByIdQuery;
import com.nerdysoft.axon.query.account.FindUserDetailsQuery;
import com.nerdysoft.entity.Account;
import com.nerdysoft.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountQueryHandler {
  private final AccountService accountService;

  private final UserDetailsService userDetailsService;

  @QueryHandler
  public Account handle(FindAccountByIdQuery query) {
    return accountService.findById(query.getAccountId());
  }

  @QueryHandler
  public Account handle(FindAccountByEmailQuery query) {
    return accountService.findByEmail(query.getEmail());
  }

  @QueryHandler
  public Account handle(FindUserDetailsQuery query) {
    return (Account) userDetailsService.loadUserByUsername(query.getEmail());
  }
}
