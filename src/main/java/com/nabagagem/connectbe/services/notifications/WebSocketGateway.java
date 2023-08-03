package com.nabagagem.connectbe.services.notifications;

import com.nabagagem.connectbe.domain.messages.ThreadMessage;
import com.nabagagem.connectbe.domain.notification.NotificationCommand;
import com.nabagagem.connectbe.entities.Message;
import com.nabagagem.connectbe.services.mappers.MessageMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty("ramifica.web-socket.enabled")
public class WebSocketGateway implements NotificationGateway {
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final MessageMapper messageMapper;
    private final Set<Action> actions = Set.of(
            Action.CREATED, Action.UPDATED);

    public void send(NotificationCommand notificationCommand, Locale locale) {
        if (actions.contains(notificationCommand.action())
                && notificationCommand.payload() instanceof Message message) {
            ThreadMessage dto = messageMapper.toDto(message);
            simpMessagingTemplate.convertAndSend(
                    "/topics/user/" + notificationCommand.profile().getId(),
                    dto);
            log.info("Web socket event sent to user {} with payload {}", notificationCommand.profile().getId(), dto);
        }
    }
}
