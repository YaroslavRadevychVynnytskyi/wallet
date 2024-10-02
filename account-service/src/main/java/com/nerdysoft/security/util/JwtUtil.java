package com.nerdysoft.security.util;

import com.nerdysoft.entity.Account;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtUtil {
    private final UserDetailsService userDetailsService;

    @Value("${application.secret-key}")
    private String secretString;

    @Value("${application.token-expiration-time}")
    private Long expiration;

    private SecretKey key;

    @Value("${application.internal-token}")
    private String internalToken;

    @PostConstruct
    private void initSecretKey() {
        key = Keys.hmacShaKeyFor(secretString.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(Account account) {
        return Jwts.builder()
            .subject(account.getEmail())
            .expiration(new Date(System.currentTimeMillis() + expiration))
            .signWith(key)
            .compact();
    }

    public boolean isTokenValid(String token) {
        Jws<Claims> claims = Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token);
        if (claims.getPayload().getExpiration().after(new Date())) {
            try {
                UserDetails userDetails = userDetailsService.loadUserByUsername(claims.getPayload().getSubject());
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities()
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);
                return true;
            } catch (UsernameNotFoundException e) {
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean isInternalTokenValid(String token) {
        return internalToken.equals(token);
    }
}
