package com.nerdysoft.dto.api.request;

public record CreateAccountRequestDto(
        String username,
        String email,
        String password
) {
}
