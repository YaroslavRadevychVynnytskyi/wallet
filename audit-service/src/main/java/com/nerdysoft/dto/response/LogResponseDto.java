package com.nerdysoft.dto.response;

import com.nerdysoft.model.activity.enums.Status;

public record LogResponseDto(String message, Status status) {
}
