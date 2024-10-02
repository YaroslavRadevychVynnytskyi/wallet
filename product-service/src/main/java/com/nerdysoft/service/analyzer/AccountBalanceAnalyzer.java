package com.nerdysoft.service.analyzer;

import java.math.BigDecimal;
import java.util.UUID;

public interface AccountBalanceAnalyzer {
    boolean isNewAccount(UUID accountId);

    boolean hasBalanceAboveForLastMonth(UUID accountId, BigDecimal threshold);

    boolean hasTurnoverAboveForLastMonth(UUID accountId, BigDecimal threshold);
}
