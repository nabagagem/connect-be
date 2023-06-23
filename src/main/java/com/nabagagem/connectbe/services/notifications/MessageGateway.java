package com.nabagagem.connectbe.services.notifications;

import com.nabagagem.connectbe.domain.NotificationCommand;
import com.nabagagem.connectbe.entities.NotificationType;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
@AllArgsConstructor
@ConditionalOnProperty("ramifica.web-socket.enabled")
public class MessageGateway implements NotificationGateway {
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final WsPayloadMapper wsPayloadMapper;

    public void send(NotificationCommand notificationCommand, Locale locale) {
        simpMessagingTemplate.convertAndSend(
                "/topics/user/" + notificationCommand.profile().getId(),
                wsPayloadMapper.toWsPayload(notificationCommand));
    }

    public record WsNotificationPayload(String targetObjectId, NotificationType type) {
    }
}
