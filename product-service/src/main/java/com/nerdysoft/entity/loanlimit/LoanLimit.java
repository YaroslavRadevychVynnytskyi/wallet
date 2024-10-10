package com.nerdysoft.entity.loanlimit;

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
import lombok.ToString;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@ToString
public class LoanLimit {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID accountId;

    @Column(nullable = false)
    private String accountEmail;

    @Column(nullable = false)
    private UUID walletId;

    @Column(nullable = false)
    private BigDecimal availableAmount;

    @Column(nullable = false)
    private BigDecimal initialAmount;

    @Column(nullable = false)
    private boolean isRepaid;

    @Enumerated(EnumType.STRING)
    private Currency currency;

    @Column(nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();

    private LocalDateTime dueDate = LocalDateTime.now().plusMonths(1).withDayOfMonth(25);

    public LoanLimit(UUID accountId, String accountEmail, UUID walletId, Currency currency, BigDecimal initialAmount) {
        this.accountId = accountId;
        this.accountEmail = accountEmail;
        this.walletId = walletId;
        this.availableAmount = initialAmount;
        this.initialAmount = initialAmount;
        this.currency = currency;
    }
}
