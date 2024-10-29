package com.nerdysoft.entity.deposit;

import com.nerdysoft.entity.deposit.enums.DepositStatus;
import com.nerdysoft.model.enums.Currency;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@Builder
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Deposit {
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
    private BigDecimal amount;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Currency currency;

    private LocalDate depositDate;

    private LocalDate maturityDate;

    private LocalDate notificationDate;

    @Column(nullable = false)
    private BigDecimal yearInterestRate;

    @Column(nullable = false)
    private BigDecimal fourMonthsInterestRate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DepositStatus depositStatus;
}
