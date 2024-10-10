package com.nerdysoft.dto.api.request;

import com.nerdysoft.dto.feign.enums.Currency;
import java.util.UUID;

public record LoanLimitRequestDto(
        UUID accountId,
        Currency currency
) {
}
