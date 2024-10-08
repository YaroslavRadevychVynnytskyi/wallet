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

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
public class LoanLimit {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID accountId;

    @Column(nullable = false)
    private UUID walletId;

    @Column(nullable = false)
    private BigDecimal availableLoanLimit;

    @Enumerated(EnumType.STRING)
    private Currency currency;

    @Column(nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();

    public LoanLimit(UUID accountId, UUID walletId, Currency currency, BigDecimal availableLoanLimit) {
        this.accountId = accountId;
        this.walletId = walletId;
        this.availableLoanLimit = availableLoanLimit;
        this.currency = currency;
    }
}
