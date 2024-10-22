package com.nerdysoft.axon.query.role;

import com.nerdysoft.model.enums.RoleName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FindRoleByNameQuery {
  private RoleName name;
}
