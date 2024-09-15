package com.nerdysoft.service;

import com.nerdysoft.dto.event.Event;
import com.nerdysoft.dto.event.activity.enums.ActionType;
import com.nerdysoft.dto.event.activity.enums.EntityType;
import java.util.Optional;
import java.util.UUID;

public interface EventProducer {
    <T extends Event> void sendEvent(T event);

    <T extends Event> void sendEvent(UUID accountID,
                                     UUID entityId,
                                     ActionType actionType,
                                     EntityType entityType,
                                     Optional<?> oldData,
                                     Optional<?> newData);
}
