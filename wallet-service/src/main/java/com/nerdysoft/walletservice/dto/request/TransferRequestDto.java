package com.nerdysoft.walletservice.dto.request;

import com.nerdysoft.walletservice.model.enums.Currency;
import java.util.UUID;

public record TransferRequestDto(UUID toWalletId, Double amount, Currency currency) {}
