package com.nerdysoft.model.security;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.security.core.userdetails.UserDetails;

public record Account(UUID accountId,
                      String fullName,
                      String email,
                      String password,
                      LocalDateTime createdAt,
                      List<Role> roles) implements UserDetails {
  @Override
  public List<Role> getAuthorities() {
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
