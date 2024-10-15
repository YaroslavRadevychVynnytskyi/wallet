package com.nerdysoft.entity.loan;

import com.nerdysoft.dto.feign.enums.Currency;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
public class LoanPayment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID accountId;

    @Column(nullable = false)
    private UUID loanId;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private Currency currency;

    @Column(nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();

    public LoanPayment(Loan loan) {
        this.accountId = loan.getAccountId();
        this.loanId = loan.getId();
        this.amount = loan.getUsdMonthlyRepaymentAmount();
        this.currency = Currency.USD;
    }
}
