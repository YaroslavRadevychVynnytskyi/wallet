package com.nerdysoft.service.strategy.handler;

import com.nerdysoft.dto.api.request.CalcCommissionRequestDto;
import java.math.BigDecimal;

public interface CommissionHandler {
    BigDecimal getCommission(CalcCommissionRequestDto message, BigDecimal amount);
}
