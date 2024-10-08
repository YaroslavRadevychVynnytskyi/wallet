package com.nerdysoft.service.strategy.handler;

import com.nerdysoft.dto.api.request.CommissionRequestMessage;
import java.math.BigDecimal;

public interface CommissionHandler {
    BigDecimal getCommission(CommissionRequestMessage message, BigDecimal amount);
}
