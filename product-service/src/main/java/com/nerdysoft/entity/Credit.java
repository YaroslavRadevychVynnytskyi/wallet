package com.nerdysoft.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class Credit {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID accountId;

    @ManyToOne
    private CreditProduct creditProduct;

    @Column(nullable = false)
    private BigDecimal creditAmount;

    @Column(nullable = false)
    private BigDecimal outstandingBalance;

    @Column(nullable = false)
    private LocalDateTime startDate = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime nextPaymentDate;

    @Column(nullable = false)
    private BigDecimal penaltyAmount = BigDecimal.valueOf(0);

    @Column(nullable = false)
    private String currency;
}
