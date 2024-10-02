package com.nerdysoft.walletservice.security.service;

import com.nerdysoft.walletservice.feign.AccountFeignClient;
import com.nerdysoft.walletservice.mapper.AccountMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
  private final AccountFeignClient accountFeignClient;

  private final AccountMapper accountMapper;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    try {
      return accountMapper.toUserDetails(accountFeignClient.getAccountByEmail(username).getBody());
    } catch (Exception e) {
      throw new UsernameNotFoundException(String.format("No user with email: %s", username));
    }
  }
}
