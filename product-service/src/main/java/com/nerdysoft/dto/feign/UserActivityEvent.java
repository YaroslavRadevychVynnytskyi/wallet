package com.nerdysoft.dto.feign;

import com.nerdysoft.dto.feign.enums.ActionType;
import com.nerdysoft.dto.feign.enums.EntityType;
import com.nerdysoft.dto.feign.enums.Status;
import java.time.LocalDateTime;
import java.util.UUID;

public record UserActivityEvent(
        UUID userId,
        ActionType actionType,
        EntityType entityType,
        UUID entityId,
        LocalDateTime timestamp,
        String oldData,
        String newData,
        Status status
) {
}
