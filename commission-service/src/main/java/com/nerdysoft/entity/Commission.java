package com.nerdysoft.entity;

import com.nerdysoft.dto.api.request.CommissionRequestMessage;
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
    private Boolean isLoanLimitUsed;

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

    public Commission(BigDecimal usdCommissionAmount, BigDecimal transferCurrencyCommissionAmount, CommissionRequestMessage commissionMessage) {
        this.usdCommissionAmount = usdCommissionAmount;
        this.senderCurrencyCommissionAmount = transferCurrencyCommissionAmount;
        transactionId = commissionMessage.getTransactionId();
        walletAmount = commissionMessage.getWalletAmount();
        isLoanLimitUsed = commissionMessage.isLoanLimitUsed();
        loanLimitAmount = commissionMessage.getLoanLimitAmount();
        fromWalletCurrency = commissionMessage.getFromWalletCurrency();
        toWalletCurrency = commissionMessage.getToWalletCurrency();
        transactionCurrency = commissionMessage.getTransactionCurrency();
    }
}
