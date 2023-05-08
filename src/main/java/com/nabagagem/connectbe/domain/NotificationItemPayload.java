package com.nabagagem.connectbe.domain;

import com.nabagagem.connectbe.entities.NotificationType;

public record NotificationItemPayload(
        String title,
        String targetObjectId,
        Boolean read,
        NotificationType type
) {
}
