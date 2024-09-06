package com.nerdysoft.dto;

import java.time.LocalDateTime;

public record AddOrUpdateRateResponseDto(
        String status,
        LocalDateTime timestamp
) {
}
