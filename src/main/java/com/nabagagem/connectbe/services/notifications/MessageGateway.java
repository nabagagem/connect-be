package com.nabagagem.connectbe.services.notifications;

import com.nabagagem.connectbe.domain.messages.ThreadMessage;
import com.nabagagem.connectbe.domain.notification.NotificationCommand;
import com.nabagagem.connectbe.entities.Message;
import com.nabagagem.connectbe.entities.NotificationType;
import com.nabagagem.connectbe.services.mappers.MessageMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Slf4j
@Component
@AllArgsConstructor
@ConditionalOnProperty("ramifica.web-socket.enabled")
public class MessageGateway implements NotificationGateway {
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final MessageMapper messageMapper;

    public void send(NotificationCommand notificationCommand, Locale locale) {
        if (notificationCommand.type() == NotificationType.NEW_MESSAGE
                && notificationCommand.payload() instanceof Message message) {
            ThreadMessage dto = messageMapper.toDto(message);
            simpMessagingTemplate.convertAndSend(
                    "/topics/user/" + notificationCommand.profile().getId(),
                    dto);
            log.info("Web socket event sent to user {} with payload {}", notificationCommand.profile().getId(), dto);
        }
    }
}
