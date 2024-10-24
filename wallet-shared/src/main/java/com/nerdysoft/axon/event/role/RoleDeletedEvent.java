package com.nerdysoft.axon.event.role;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RoleDeletedEvent {
  private Integer roleId;
}
