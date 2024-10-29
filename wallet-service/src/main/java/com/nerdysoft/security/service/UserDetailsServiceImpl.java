package com.nerdysoft.security.service;

import com.nerdysoft.feign.AccountFeignClient;
import com.nerdysoft.model.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
  private final AccountFeignClient accountFeignClient;

  @Override
  public Account loadUserByUsername(String username) throws UsernameNotFoundException {
    try {
      return accountFeignClient.getAccountByEmail(username).getBody();
    } catch (Exception e) {
      throw new UsernameNotFoundException(String.format("No user with email: %s", username));
    }
  }
}
