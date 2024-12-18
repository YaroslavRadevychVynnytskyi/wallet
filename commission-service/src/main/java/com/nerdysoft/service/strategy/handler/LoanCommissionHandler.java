package com.nerdysoft.service.strategy.handler;

import com.nerdysoft.dto.api.request.CalcCommissionRequestDto;
import java.math.BigDecimal;
import org.springframework.stereotype.Component;

@Component
public class LoanCommissionHandler implements CommissionHandler {
    @Override
    public BigDecimal getCommission(CalcCommissionRequestDto requestDto, BigDecimal amount) {
        return BigDecimal.valueOf(0.04).multiply(amount);
    }
}
