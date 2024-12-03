package com.nerdysoft.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Commission {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID commissionId;

    @Column(nullable = false)
    private UUID accountId;

    @Column(nullable = false)
    private UUID transactionId;

    @Column(nullable = false)
    private BigDecimal usedWalletOwnAmount;

    @Column(nullable = false)
    private boolean loanLimitUsed;

    @Column(nullable = false)
    private BigDecimal usedLoanLimitAmount;

    @Column(nullable = false)
    private BigDecimal commissionAmount;
}
