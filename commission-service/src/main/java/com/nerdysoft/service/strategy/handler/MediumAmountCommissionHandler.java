package com.nerdysoft.service.strategy.handler;

import java.math.BigDecimal;
import org.springframework.stereotype.Component;

@Component
public class MediumAmountCommissionHandler implements CommissionHandler {
    @Override
    public BigDecimal getCommission(String fromWalletCurrency, String toWalletCurrency, String transactionCurrency, BigDecimal amount) {
        if (!fromWalletCurrency.equals(transactionCurrency) || !toWalletCurrency.equals(transactionCurrency)) {
            return BigDecimal.valueOf(0.015).multiply(amount);
        }
        return BigDecimal.valueOf(0.0075).multiply(amount);
    }
}
