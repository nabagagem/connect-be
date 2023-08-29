package com.nabagagem.connectbe.services.notifications;

import com.nabagagem.connectbe.domain.messages.UserChatStatusCommand;
import com.nabagagem.connectbe.domain.notification.NotificationCommand;
import com.nabagagem.connectbe.entities.Message;
import com.nabagagem.connectbe.services.mappers.MessageMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty("ramifica.web-socket.enabled")
public class WebSocketGateway implements NotificationGateway {
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final MessageMapper messageMapper;

    public void send(NotificationCommand notificationCommand, Locale locale) {
        if (notificationCommand.payload() instanceof Message message) {
            WsMessage wsMessage = new WsMessage(
                    messageMapper.toDto(message), notificationCommand.action());
            simpMessagingTemplate.convertAndSend(
                    "/topics/user/" + notificationCommand.profile().getId(),
                    wsMessage);
            log.info("Web socket event sent to user {} with payload {}", notificationCommand.profile().getId(), wsMessage);
            return;
        }
        if (notificationCommand.payload() instanceof UserChatStatusCommand userChatStatusCommand) {
            WsUserStatus payload = new WsUserStatus(userChatStatusCommand.status(), notificationCommand.targetObjectId());
            simpMessagingTemplate.convertAndSend(
                    "/topics/user/" + notificationCommand.profile().getId(),
                    payload);
            log.info("Web socket event sent to user {} with payload {}", notificationCommand.profile().getId(), payload);
        }
    }
}
