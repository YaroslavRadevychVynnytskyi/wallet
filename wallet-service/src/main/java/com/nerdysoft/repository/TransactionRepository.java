package com.nerdysoft.repository;

import com.nerdysoft.entity.Transaction;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    List<Transaction> findAllByWalletId(UUID walletId);

    @Query("""
        SELECT t FROM Transaction t
        WHERE t.accountId = :accountId
        AND t.status = 'SUCCESS'
        AND t.createdAt >= :dateFrom
        """)
    List<Transaction> findSuccessfulTransactionsByAccountIdInLastMonth(UUID accountId, LocalDateTime dateFrom);
}
