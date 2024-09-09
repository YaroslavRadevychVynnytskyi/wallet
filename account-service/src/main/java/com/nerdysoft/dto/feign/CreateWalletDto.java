package com.nerdysoft.dto.feign;

import java.util.UUID;

public record CreateWalletDto(
        UUID accountId,
        Currency currency
) {
}
