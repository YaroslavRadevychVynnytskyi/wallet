package com.nerdysoft.security.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TokenValidatorService {
    @Value("${application.secret-key}")
    private String secretString;

    @Value("${application.internal-token}")
    private String internalToken;

    private SecretKey key;

    @PostConstruct
    private void initSecretKey() {
        key = Keys.hmacShaKeyFor(secretString.getBytes(StandardCharsets.UTF_8));
    }

    public boolean isTokenValid(String token) {
      final Jws<Claims> claims = Jwts.parser()
          .verifyWith(key)
          .build()
          .parseSignedClaims(token);
      return claims.getPayload().getExpiration().after(new Date());
    }

    public boolean isInternalTokenValid(String token) {
        return internalToken.equals(token);
    }
}
