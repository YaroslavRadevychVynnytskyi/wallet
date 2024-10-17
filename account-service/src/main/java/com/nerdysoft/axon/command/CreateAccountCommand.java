package com.nerdysoft.axon.command;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CreateAccountCommand {
  private final String email;

  private final String fullName;

  private final String password;
}
