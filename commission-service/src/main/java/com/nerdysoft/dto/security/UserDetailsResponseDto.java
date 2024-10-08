package com.nerdysoft.dto.security;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record UserDetailsResponseDto(UUID accountId,
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
                                     boolean accountNonLocked) {
}
