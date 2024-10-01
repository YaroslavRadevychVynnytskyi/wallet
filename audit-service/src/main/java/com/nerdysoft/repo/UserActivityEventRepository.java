package com.nerdysoft.repo;

import com.nerdysoft.model.activity.UserActivityEvent;
import java.util.List;
import java.util.UUID;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserActivityEventRepository extends MongoRepository<UserActivityEvent, UUID> {
    List<UserActivityEvent> findAllByUserId(UUID userId);
}
