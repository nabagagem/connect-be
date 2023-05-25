package com.nabagagem.connectbe.services.notifications;

import com.nabagagem.connectbe.domain.NotificationCommand;
import com.nabagagem.connectbe.entities.NotificationType;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@ConditionalOnProperty("web-socket.enabled")
public class MessageGateway {
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final WsPayloadMapper wsPayloadMapper;

    public void send(NotificationCommand notificationCommand) {
        simpMessagingTemplate.convertAndSend(
                "/topics/user/" + notificationCommand.profile().getId(),
                wsPayloadMapper.toWsPayload(notificationCommand));
    }

    public record WsNotificationPayload(String targetObjectId, NotificationType type) {
    }
}
