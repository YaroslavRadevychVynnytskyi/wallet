package com.nerdysoft.service;

import com.nerdysoft.entity.Role;
import com.nerdysoft.model.enums.RoleName;

public interface RoleService {
  Role findById(Integer id);

  Role findByName(RoleName name);

  Role create(RoleName name);

  Role update(Integer id, RoleName name);

  String delete(Integer id);

  boolean hasDbData();
}
