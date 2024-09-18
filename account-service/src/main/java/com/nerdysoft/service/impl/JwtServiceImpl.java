package com.nerdysoft.service.impl;

import com.nerdysoft.entity.Account;
import com.nerdysoft.entity.Role;
import com.nerdysoft.service.JwtService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtServiceImpl implements JwtService {
  @Value("${application.secret-key}")
  private String secretKey;

  @Value("${application.token-expiration-time}")
  private Long tokenExpirationTime;

  @Override
  public String generateJwtToken(Account account) {
    List<String> authorities = account.getRoles().stream()
        .map(Role::getAuthority)
        .toList();
    SecretKey secretKey = Keys.hmacShaKeyFor(this.secretKey.getBytes(StandardCharsets.UTF_8));
    return Jwts.builder()
        .subject(account.getEmail())
        .claim("accountId", account.getAccountId())
        .claim("authorities", authorities)
        .expiration(new Date(new Date().getTime() + tokenExpirationTime))
        .signWith(secretKey)
        .compact();
  }
}
