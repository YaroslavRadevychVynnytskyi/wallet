package com.nerdysoft.model;

import com.nerdysoft.model.enums.Currency;
import com.nerdysoft.model.enums.TransactionStatus;
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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID transactionId;

  @Column(nullable = false)
  private UUID walletId;

  private UUID toWalletId;

  @Column(nullable = false)
  private BigDecimal walletBalance;

  @Column(nullable = false)
  private BigDecimal amount;

  @Enumerated(EnumType.STRING)
  private Currency currency;

  @Enumerated(EnumType.STRING)
  private TransactionStatus status;

  private final LocalDateTime createdAt = LocalDateTime.now();
}