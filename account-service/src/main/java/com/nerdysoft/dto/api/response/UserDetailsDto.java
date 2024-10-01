package com.nerdysoft.dto.api.response;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record UserDetailsDto(UUID accountId,
                             String fullName,
                             String email,
                             String password,
                             LocalDateTime createdAt,
                             Set<RoleDto> roles,
                             Set<String> authorities,
                             String username,
                             boolean enabled,
                             boolean accountNonExpired,
                             boolean credentialsNonExpired,
                             boolean accountNonLocked) {}
