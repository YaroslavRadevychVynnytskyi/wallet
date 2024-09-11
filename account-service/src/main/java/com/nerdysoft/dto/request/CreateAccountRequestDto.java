package com.nerdysoft.dto.request;

public record CreateAccountRequestDto(
        String username,
        String email,
        String password
) {
}
