package com.application.dto;

public record CreateAccountRequestDto(
        String username,
        String email,
        String password
) {
}
