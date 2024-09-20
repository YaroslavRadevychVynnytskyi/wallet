package com.nerdysoft.dto.api.request;

public record UpdateAccountRequestDto(
        String fullName,
        String email
) {
}
