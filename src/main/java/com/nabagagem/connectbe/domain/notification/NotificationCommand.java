package com.nabagagem.connectbe.domain.notification;

import com.nabagagem.connectbe.entities.ConnectProfile;
import com.nabagagem.connectbe.entities.NotificationType;

public record NotificationCommand(
        ConnectProfile profile,
        String title,
        String targetObjectId,
        NotificationType type,
        Object payload
) {
}
