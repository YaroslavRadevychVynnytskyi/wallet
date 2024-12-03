package com.nerdysoft.dto.api.request;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaveCommissionRequestDto {
    private UUID accountId;
    private UUID transactionId;
    private BigDecimal usedWalletOwnAmount;
    private boolean loanLimitUsed;
    private BigDecimal usedLoanLimitAmount;
    private BigDecimal commissionAmount;
}
