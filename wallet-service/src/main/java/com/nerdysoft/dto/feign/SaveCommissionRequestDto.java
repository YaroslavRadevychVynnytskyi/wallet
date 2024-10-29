package com.nerdysoft.dto.feign;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.Data;

@Data
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

    public SaveCommissionRequestDto(UUID transactionId, CalcCommissionResponseDto responseDto) {
        this.transactionId = transactionId;
        this.usdCommission = responseDto.getUsdCommission();
        this.originalCurrencyCommission = responseDto.getOriginalCurrencyCommission();
        this.walletAmount = responseDto.getWalletAmount();
        this.isLoanLimitUsed = responseDto.isLoanLimitUsed();
        this.loanLimitAmount = responseDto.getLoanLimitAmount();
        this.fromWalletCurrency = responseDto.getFromWalletCurrency();
        this.toWalletCurrency = responseDto.getToWalletCurrency();
        this.transactionCurrency = responseDto.getTransactionCurrency();
    }
}
