package com.nerdysoft.dto.api.request;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class CalcCommissionRequestDto {
    private BigDecimal usedWalletOwnAmount;
    private boolean loanLimitUsed;
    private BigDecimal usedLoanLimitAmount;
    private String fromWalletCurrency;
    private String toWalletCurrency;
    private String transactionCurrency;
}
