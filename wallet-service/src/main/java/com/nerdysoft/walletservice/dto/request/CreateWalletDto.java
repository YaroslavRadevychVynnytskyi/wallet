package com.nerdysoft.walletservice.dto.request;

import com.nerdysoft.walletservice.model.enums.Currency;
import java.util.UUID;

public record CreateWalletDto(UUID accountId, Currency currency) {}
