package com.nerdysoft.dto.response;

import com.nerdysoft.entity.activity.enums.Status;

public record LogResponseDto(String message, Status status) {
}
