package com.nerdysoft.entity.security;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public record Account(UUID accountId,
                      String fullName,
                      String email,
                      String password,
                      LocalDateTime createdAt,
                      Set<Role> roles) implements UserDetails {
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return roles();
  }

  @Override
  public String getPassword() {
    return password();
  }

  @Override
  public String getUsername() {
    return email();
  }
}
