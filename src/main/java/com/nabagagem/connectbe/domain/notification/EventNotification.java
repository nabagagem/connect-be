package com.nabagagem.connectbe.domain.notification;

import com.nabagagem.connectbe.services.notifications.PublishNotification;

public record EventNotification(PublishNotification notification, Object result) {
}
