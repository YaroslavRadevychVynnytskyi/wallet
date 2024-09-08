package com.nerdysoft.dto.response;

import java.time.LocalDateTime;

public record AddOrUpdateRateResponseDto(
        String status,
        LocalDateTime timestamp
) {
}
