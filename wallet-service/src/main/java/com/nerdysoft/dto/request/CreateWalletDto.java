package com.nerdysoft.dto.request;

import com.nerdysoft.entity.enums.Currency;
import java.util.UUID;

public record CreateWalletDto(UUID accountId, Currency currency) {}
