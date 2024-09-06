package com.nerdysoft.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "exchange_rates")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "exchangeRateId")
public class ExchangeRate {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID exchangeRateId;

    @Column(name = "from_currency", nullable = false)
    private String fromCurrency;

    @Column(name = "to_currency", nullable = false)
    private String toCurrency;

    @Column(name = "exchange_rate", nullable = false)
    private BigDecimal exchangeRate;

    @Column(nullable = false)
    private LocalDateTime timestamp;
}
