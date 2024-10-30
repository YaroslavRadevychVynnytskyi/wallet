package com.nerdysoft.dto.api.request;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SaveCommissionRequestDto {
    private UUID transactionId;
    private BigDecimal usdCommission;
    private BigDecimal originalCurrencyCommission;
    private BigDecimal walletAmount;
    private boolean isLoanLimitUsed;
    private BigDecimal loanLimitAmount;
    private String fromWalletCurrency;
    private String toWalletCurrency;
    private String transactionCurrency;
}
