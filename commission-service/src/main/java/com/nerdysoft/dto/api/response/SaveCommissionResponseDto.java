package com.nerdysoft.dto.api.response;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.Data;

@Data
public class SaveCommissionResponseDto {
    private UUID commissionId;
    private UUID transactionId;
    private BigDecimal usedWalletOwnAmount;
    private boolean loanLimitUsed;
    private BigDecimal usedLoanLimitAmount;
    private BigDecimal usdCommissionAmount;
    private BigDecimal senderCurrencyCommissionAmount;
    private String fromWalletCurrency;
    private String toWalletCurrency;
    private String transactionCurrency;
}
