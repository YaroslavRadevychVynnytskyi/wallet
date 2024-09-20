package com.nerdysoft.service;

import com.nerdysoft.entity.Role;
import com.nerdysoft.entity.enums.RoleName;

public interface RoleService {
  Role getRoleById(Integer id);

  Role getRoleByName(RoleName name);

  Role create(RoleName name);

  Role update(RoleName name);

  String delete(Integer id);
}
