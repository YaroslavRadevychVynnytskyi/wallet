package com.nerdysoft.entity.loanlimit;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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
public class LoanLimit {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID accountId;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private BigDecimal availableAmount;

    @Column(nullable = false)
    private BigDecimal initialAmount;

    @Column(nullable = false)
    private boolean repaid;

    private final LocalDateTime dueDate = LocalDateTime.now().plusMonths(1).withDayOfMonth(25);
}
