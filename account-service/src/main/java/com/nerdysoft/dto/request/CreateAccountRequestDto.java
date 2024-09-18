package com.nerdysoft.dto.request;

public record CreateAccountRequestDto(
        String fullName,
        String email,
        String password
) {
}
