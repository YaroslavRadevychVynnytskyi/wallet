package com.nerdysoft.dto.api.request.deposit;

import com.nerdysoft.dto.feign.enums.Currency;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class DepositRequestDto {
    private BigDecimal amount;
    private Currency walletCurrency;
}
