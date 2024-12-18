package com.nerdysoft.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RoleName {
  USER("USER"),
  ADMIN("ADMIN");

  private final String name;
}
