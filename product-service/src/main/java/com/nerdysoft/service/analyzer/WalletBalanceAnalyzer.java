package com.nerdysoft.service.analyzer;

import java.math.BigDecimal;
import java.util.UUID;

public interface WalletBalanceAnalyzer {
    BigDecimal getMaxBalanceForLastMonth(UUID walletId);

    BigDecimal getTurnoverForLastMonth(UUID walletId);
}
