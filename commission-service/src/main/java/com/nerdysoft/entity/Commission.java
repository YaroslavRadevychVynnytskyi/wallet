package com.nerdysoft.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "commissionId")
@NoArgsConstructor
public class Commission {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID commissionId;

    @Column(nullable = false)
    private UUID transactionId;

    @Column(nullable = false)
    private BigDecimal walletAmount;

    @Column(nullable = false)
    private boolean isLoanLimitUsed;

    @Column(nullable = false)
    private BigDecimal loanLimitAmount;

    @Column(nullable = false)
    private BigDecimal usdCommissionAmount;

    @Column(nullable = false)
    private BigDecimal senderCurrencyCommissionAmount;

    @Column(nullable = false)
    private String fromWalletCurrency;

    @Column(nullable = false)
    private String toWalletCurrency;

    @Column(nullable = false)
    private String transactionCurrency;
}
