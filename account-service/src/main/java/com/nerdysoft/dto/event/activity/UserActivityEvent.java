package com.nerdysoft.dto.event.activity;

import com.nerdysoft.dto.event.Event;
import com.nerdysoft.dto.event.activity.enums.ActionType;
import com.nerdysoft.dto.event.activity.enums.EntityType;
import com.nerdysoft.dto.event.activity.enums.Status;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserActivityEvent implements Event, Serializable {
    private UUID userId;
    private ActionType actionType;
    private EntityType entityType;
    private UUID entityId;
    private LocalDateTime timestamp;
    private String oldData;
    private String newData;
    private Status status;
}
