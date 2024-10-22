package com.nerdysoft.axon.query.role;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FindRoleByIdQuery {
  private Integer roleId;
}
