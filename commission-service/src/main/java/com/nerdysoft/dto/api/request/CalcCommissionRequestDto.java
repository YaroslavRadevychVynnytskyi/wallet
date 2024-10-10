package com.nerdysoft.dto.api.request;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class CalcCommissionRequestDto {
    private BigDecimal walletAmount;
    private boolean isLoanLimitUsed;
    private BigDecimal loanLimitAmount;
    private String fromWalletCurrency;
    private String toWalletCurrency;
    private String transactionCurrency;
}
