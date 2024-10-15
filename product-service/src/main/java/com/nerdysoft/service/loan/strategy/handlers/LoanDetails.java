package com.nerdysoft.service.loan.strategy.handlers;

import com.nerdysoft.entity.loan.enums.ApprovalStatus;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoanDetails {
    private BigDecimal repaymentTermInMonths;
    private BigDecimal repaymentAmount;
    private ApprovalStatus approvalStatus;
    private BigDecimal interestRate;
}
