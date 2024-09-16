package com.nerdysoft.dto.api.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record AccountResponseDto(
        UUID accountId,
        String username,
        String email,
        LocalDateTime createdAt
) {
}