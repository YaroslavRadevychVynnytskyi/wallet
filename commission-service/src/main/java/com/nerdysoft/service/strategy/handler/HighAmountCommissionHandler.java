package com.nerdysoft.service.strategy.handler;

import com.nerdysoft.dto.api.request.CalcCommissionRequestDto;
import java.math.BigDecimal;
import org.springframework.stereotype.Component;

@Component
public class HighAmountCommissionHandler implements CommissionHandler {
    @Override
    public BigDecimal getCommission(CalcCommissionRequestDto requestDto, BigDecimal amount) {
        if (!requestDto.getFromWalletCurrency().equals(requestDto.getToWalletCurrency()) || !requestDto.getToWalletCurrency().equals(requestDto.getTransactionCurrency())) {
            return BigDecimal.valueOf(0.01).multiply(amount);
        }
        return BigDecimal.valueOf(0.005).multiply(amount);
    }
}
