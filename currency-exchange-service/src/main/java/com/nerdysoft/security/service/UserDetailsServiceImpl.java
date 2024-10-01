package com.nerdysoft.security.service;

import com.nerdysoft.feign.AccountFeignClient;
import com.nerdysoft.mapper.AccountMapper;
import feign.FeignException;
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
    } catch (FeignException e) {
      throw new UsernameNotFoundException(String.format("No user with email: %s", username));
    }
  }
}
