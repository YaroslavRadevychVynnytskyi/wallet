package com.nerdysoft.service;

import com.nerdysoft.entity.Account;

public interface JwtService {
  String generateJwtToken(Account account);
}
