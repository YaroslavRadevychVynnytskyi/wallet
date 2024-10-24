package com.nerdysoft.axon.event.role;

import com.nerdysoft.model.enums.RoleName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RoleCreatedEvent {
  private Integer roleId;

  private RoleName name;
}
