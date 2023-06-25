package com.nabagagem.connectbe.domain;

import com.nabagagem.connectbe.services.notifications.PublishNotification;

public record EventNotification(PublishNotification notification, Object result) {
}
