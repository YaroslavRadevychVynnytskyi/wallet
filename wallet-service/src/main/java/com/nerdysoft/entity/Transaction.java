package com.nerdysoft.entity;

import com.nerdysoft.entity.enums.Currency;
import com.nerdysoft.entity.enums.TransactionStatus;
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

  public Transaction(UUID transactionId, BigDecimal amount, Currency currency, TransactionStatus status, BigDecimal walletBalance) {
    this.transactionId = transactionId;
    this.amount = amount;
    this.currency = currency;
    this.status = status;
    this.walletBalance = walletBalance;
  }

  public Transaction(UUID transactionId, BigDecimal amount, Currency currency, TransactionStatus status, UUID walletId, BigDecimal walletBalance) {
    this(transactionId, amount, currency, status, walletBalance);
    this.walletId = walletId;
  }
}
