package com.nerdysoft.entity.loan;

import com.nerdysoft.model.enums.ApprovalStatus;
import com.nerdysoft.model.enums.Currency;
import com.nerdysoft.model.enums.PaymentType;
import com.nerdysoft.model.enums.RepaymentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID accountId;

    @Column(nullable = false)
    private String accountEmail;

    @Column(nullable = false)
    private UUID walletId;

    @Enumerated(EnumType.STRING)
    private Currency walletCurrency;

    @Enumerated(EnumType.STRING)
    private ApprovalStatus approvalStatus;

    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;

    @Column(nullable = false)
    private BigDecimal usdLoanAmount;

    @Column(nullable = false)
    private BigDecimal walletCurrencyLoanAmount;

    @Column(nullable = false)
    private BigDecimal interestRate;

    @Column(nullable = false)
    private BigDecimal usdRepaymentAmount;

    @Column(nullable = false)
    private BigDecimal walletCurrencyRepaymentAmount;

    @Column(nullable = false)
    private BigDecimal usdRemainingRepaymentAmount;

    @Column(nullable = false)
    private BigDecimal repaymentTermInMonths;

    @Column(nullable = false)
    private BigDecimal usdMonthlyRepaymentAmount;

    @Column(nullable = false)
    private BigDecimal totalPaymentsMade;

    @Enumerated(EnumType.STRING)
    private RepaymentStatus repaymentStatus;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    private LocalDate nextPayment;

    private LocalDateTime dueDate;
}
