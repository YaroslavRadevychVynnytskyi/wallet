package com.nerdysoft.entity;

import org.springframework.security.core.GrantedAuthority;

public record Role(String name) implements GrantedAuthority {
  @Override
  public String getAuthority() {
    return String.format("ROLE_%s", name());
  }
}
