package com.nerdysoft.axon.event.loan;

import com.nerdysoft.model.enums.ApprovalStatus;
import com.nerdysoft.model.enums.Currency;
import com.nerdysoft.model.enums.PaymentType;
import com.nerdysoft.model.enums.RepaymentStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Data;

@Data
public class ApplyLoanEvent {
    private UUID id;
    private UUID accountId;
    private String accountEmail;
    private UUID walletId;
    private Currency walletCurrency;
    private ApprovalStatus approvalStatus;
    private PaymentType paymentType;
    private BigDecimal usdLoanAmount;
    private BigDecimal walletCurrencyLoanAmount;
    private BigDecimal interestRate;
    private BigDecimal usdRepaymentAmount;
    private BigDecimal walletCurrencyRepaymentAmount;
    private BigDecimal usdRemainingRepaymentAmount;
    private BigDecimal repaymentTermInMonths;
    private BigDecimal usdMonthlyRepaymentAmount;
    private BigDecimal totalPaymentsMade;
    private RepaymentStatus repaymentStatus;
    private LocalDateTime timestamp;
    private LocalDate nextPayment;
    private LocalDateTime dueDate;
}
