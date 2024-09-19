package com.nerdysoft.dto;

import com.nerdysoft.entity.activity.enums.Status;

public record LogResponseDto(String message, Status status) {
}
