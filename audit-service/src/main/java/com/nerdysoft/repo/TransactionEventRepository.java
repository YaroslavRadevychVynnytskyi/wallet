package com.nerdysoft.repo;

import com.nerdysoft.entity.TransactionEvent;
import java.util.UUID;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TransactionEventRepository extends MongoRepository<TransactionEvent, UUID> {
}
