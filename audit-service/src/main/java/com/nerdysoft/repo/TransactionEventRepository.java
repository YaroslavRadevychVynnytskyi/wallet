package com.nerdysoft.repo;

import com.nerdysoft.model.transaction.TransactionEvent;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TransactionEventRepository extends MongoRepository<TransactionEvent, UUID> {
    Optional<TransactionEvent> findByTransactionId(UUID transactionId);
}
