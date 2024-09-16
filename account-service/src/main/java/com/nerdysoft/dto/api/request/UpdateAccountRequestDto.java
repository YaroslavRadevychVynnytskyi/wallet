package com.nerdysoft.dto.api.request;

public record UpdateAccountRequestDto(
        String username,
        String email
) {
}
