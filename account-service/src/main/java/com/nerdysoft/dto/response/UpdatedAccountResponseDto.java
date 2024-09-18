package com.nerdysoft.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record UpdatedAccountResponseDto(
        UUID accountId,
        String fullName,
        String email,
        LocalDateTime updatedAt
) {
}
