package com.nerdysoft.dto.api.response;

import com.nerdysoft.dto.api.request.CalcCommissionRequestDto;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CalcCommissionResponseDto {
    private BigDecimal usdCommission;
    private BigDecimal originalCurrencyCommission;
    private BigDecimal walletAmount;
    private boolean isLoanLimitUsed;
    private BigDecimal loanLimitAmount;
    private String fromWalletCurrency;
    private String toWalletCurrency;
    private String transactionCurrency;

    public CalcCommissionResponseDto(BigDecimal usdCommission, BigDecimal originalCurrencyCommission, CalcCommissionRequestDto requestDto) {
        this.usdCommission = usdCommission;
        this.originalCurrencyCommission = originalCurrencyCommission;
        this.walletAmount = requestDto.getWalletAmount();
        this.isLoanLimitUsed = requestDto.isLoanLimitUsed();
        this.loanLimitAmount = requestDto.getLoanLimitAmount();
        this.fromWalletCurrency = requestDto.getFromWalletCurrency();
        this.toWalletCurrency = requestDto.getToWalletCurrency();
        this.transactionCurrency = requestDto.getTransactionCurrency();
    }
}
