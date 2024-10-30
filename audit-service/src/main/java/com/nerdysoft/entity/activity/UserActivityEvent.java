package com.nerdysoft.entity.activity;

import com.nerdysoft.entity.activity.enums.ActionType;
import com.nerdysoft.entity.activity.enums.EntityType;
import com.nerdysoft.entity.activity.enums.Status;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "user_activity_events")
@Getter
@Setter
@EqualsAndHashCode(of = "userId")
public class UserActivityEvent {
    private UUID userId;
    private ActionType actionType;
    private EntityType entityType;
    private UUID entityId;
    private LocalDateTime timestamp;
    private String oldData;
    private String newData;
    private Status status;
}
