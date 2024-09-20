package com.nerdysoft.dto.api.request;

public record CreateAccountRequestDto(
        String fullName,
        String email,
        String password
) {
}
