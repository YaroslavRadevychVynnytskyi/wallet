package com.nerdysoft.service.strategy.handler;

import java.math.BigDecimal;

public interface CommissionHandler {
    BigDecimal getCommission(String fromWalletCurrency, String toWalletCurrency, String transactionCurrency, BigDecimal amount);
}
