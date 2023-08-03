package com.nabagagem.connectbe.domain.notification;

import com.nabagagem.connectbe.entities.ConnectProfile;
import com.nabagagem.connectbe.services.notifications.Action;

public record NotificationCommand(
        ConnectProfile profile,
        String title,
        String targetObjectId,
        Action action,
        Object payload
) {
}
