package com.nerdysoft.dto.request;

public record UpdateAccountRequestDto(
        String fullName,
        String email
) {
}
