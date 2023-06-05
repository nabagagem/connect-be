package com.nabagagem.connectbe.services.notifications;

import com.nabagagem.connectbe.domain.NotificationCommand;

import java.util.Locale;

public interface NotificationGateway {
    void send(NotificationCommand notificationCommand, Locale locale);
}
