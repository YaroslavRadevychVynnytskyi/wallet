package com.nerdysoft.service.analyzer;

import com.nerdysoft.dto.feign.enums.Currency;
import java.math.BigDecimal;
import java.util.UUID;

public interface WalletBalanceAnalyzer {
    BigDecimal getMaxBalanceForLastMonth(UUID walletId);

    BigDecimal getTurnoverForLastMonth(UUID walletId, Currency walletCurrency);
}
