package com.nerdysoft.axon.handler;

import com.nerdysoft.axon.query.role.FindRoleByIdQuery;
import com.nerdysoft.axon.query.role.FindRoleByNameQuery;
import com.nerdysoft.entity.Role;
import com.nerdysoft.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoleQueryHandler {
  private final RoleService roleService;

  @QueryHandler
  public Role handle(FindRoleByIdQuery query) {
    return roleService.findById(query.getRoleId());
  }

  @QueryHandler
  public Role handle(FindRoleByNameQuery query) {
    return roleService.findByName(query.getName());
  }
}
