package com.nerdysoft.walletservice.repository;

import com.nerdysoft.walletservice.model.Wallet;
import com.nerdysoft.walletservice.model.enums.Currency;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, UUID> {
  @Query("SELECT COUNT(w) = 1 FROM Wallet w WHERE w.accountId = :accountId AND w.currency = :currency")
  boolean hasAccountWalletOnThisCurrency(UUID accountId, Currency currency);
}
