package com.nerdysoft.service.strategy.handler;

import com.nerdysoft.dto.api.request.CommissionRequestMessage;
import java.math.BigDecimal;
import org.springframework.stereotype.Component;

@Component
public class LowAmountCommissionHandler implements CommissionHandler {
    @Override
    public BigDecimal getCommission(CommissionRequestMessage message, BigDecimal amount) {
        if (!message.getFromWalletCurrency().equals(message.getToWalletCurrency()) || !message.getToWalletCurrency().equals(message.getTransactionCurrency())) {
            return BigDecimal.valueOf(0.02).multiply(amount);
        }
        return BigDecimal.valueOf(0.01).multiply(amount);
    }
}
