package com.nerdysoft.walletservice.model;

import com.nerdysoft.walletservice.model.enums.Currency;
import com.nerdysoft.walletservice.model.enums.TransactionStatus;
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
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Transaction {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID transactionId;

  @Column(nullable = false)
  private UUID walletId;

  private UUID toWalletId;

  @Column(nullable = false)
  private BigDecimal amount;

  @Enumerated(EnumType.STRING)
  private Currency currency;

  @Enumerated(EnumType.STRING)
  private TransactionStatus status;

  private final LocalDate createdAt = LocalDate.now();

  public Transaction(UUID walletId, BigDecimal amount, Currency currency, TransactionStatus status) {
    this.walletId = walletId;
    this.amount = amount;
    this.currency = currency;
    this.status = status;
  }

  public Transaction(UUID walletId, BigDecimal amount, Currency currency, TransactionStatus status, UUID toWalletId) {
    this(walletId, amount, currency, status);
    this.toWalletId = toWalletId;
  }
}
