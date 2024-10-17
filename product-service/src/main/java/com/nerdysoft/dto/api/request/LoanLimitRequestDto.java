package com.nerdysoft.dto.api.request;

import com.nerdysoft.model.enums.Currency;
import java.util.UUID;

public record LoanLimitRequestDto(
        UUID accountId,
        Currency currency
) {
}
