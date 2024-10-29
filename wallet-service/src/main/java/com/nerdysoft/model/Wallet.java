package com.nerdysoft.model;

import com.nerdysoft.model.enums.Currency;
import com.nerdysoft.dto.request.CreateWalletDto;
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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Wallet {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID walletId;

  @Column(nullable = false)
  private UUID accountId;

  @Column(nullable = false)
  private BigDecimal balance;

  @Enumerated(EnumType.STRING)
  private Currency currency;

  private final LocalDateTime createdAt = LocalDateTime.now();

  public Wallet(CreateWalletDto createWalletDto) {
    this.accountId = createWalletDto.accountId();
    this.balance = BigDecimal.valueOf(0.0);
    this.currency = createWalletDto.currency();
  }
}
