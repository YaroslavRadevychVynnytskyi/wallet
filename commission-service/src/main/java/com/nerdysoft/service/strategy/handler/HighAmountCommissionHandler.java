package com.nerdysoft.service.strategy.handler;

import java.math.BigDecimal;
import org.springframework.stereotype.Component;

@Component
public class HighAmountCommissionHandler implements CommissionHandler {
    @Override
    public BigDecimal getCommission(String fromWalletCurrency, String toWalletCurrency, String transactionCurrency, BigDecimal amount) {
        if (!fromWalletCurrency.equals(transactionCurrency) || !toWalletCurrency.equals(transactionCurrency)) {
            return BigDecimal.valueOf(0.01).multiply(amount);
        }
        return BigDecimal.valueOf(0.005).multiply(amount);
    }
}