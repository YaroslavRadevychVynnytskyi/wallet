package com.nerdysoft.dto.event.activity;

import com.nerdysoft.dto.event.Event;
import com.nerdysoft.dto.event.activity.enums.ActionType;
import com.nerdysoft.dto.event.activity.enums.EntityType;
import com.nerdysoft.dto.event.activity.enums.Status;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserActivityEvent extends Event implements Serializable {
    private final UUID userId;
    private final ActionType actionType;
    private final EntityType entityType;
    private final UUID entityId;
    private final LocalDateTime timestamp;
    private final String oldData;
    private final String newData;
    private final Status status;
}
