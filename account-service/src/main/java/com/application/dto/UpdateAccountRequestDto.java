package com.application.dto;

public record UpdateAccountRequestDto(
        String username,
        String email
) {
}
