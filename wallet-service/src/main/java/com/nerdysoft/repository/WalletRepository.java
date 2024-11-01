package com.nerdysoft.repository;

import com.nerdysoft.model.enums.Currency;
import com.nerdysoft.entity.Wallet;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, UUID> {
  @Query("SELECT COUNT(w) = 1 FROM Wallet w WHERE w.accountId = :accountId AND w.currency = :currency")
  boolean hasAccountWalletOnThisCurrency(UUID accountId, com.nerdysoft.model.enums.Currency currency);

  Optional<Wallet> findByAccountIdAndCurrency(UUID accountId, Currency currency);
}
