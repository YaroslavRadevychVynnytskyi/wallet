package com.nerdysoft.axon.command.role;

import com.nerdysoft.model.enums.RoleName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateRoleCommand {
  private RoleName name;
}
