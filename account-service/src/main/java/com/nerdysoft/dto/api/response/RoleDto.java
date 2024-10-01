package com.nerdysoft.dto.api.response;

import com.nerdysoft.entity.enums.RoleName;

public record RoleDto(Integer id,
                      RoleName name,
                      String authority) {}
