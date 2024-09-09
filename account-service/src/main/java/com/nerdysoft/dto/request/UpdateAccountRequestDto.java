package com.nerdysoft.dto.request;

public record UpdateAccountRequestDto(
        String username,
        String email
) {
}
