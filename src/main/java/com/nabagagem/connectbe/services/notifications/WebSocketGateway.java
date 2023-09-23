package com.nabagagem.connectbe.services.notifications;

import com.nabagagem.connectbe.domain.messages.UserChatStatusCommand;
import com.nabagagem.connectbe.domain.notification.NotificationCommand;
import com.nabagagem.connectbe.entities.Message;
import com.nabagagem.connectbe.repos.ProfileRepo;
import com.nabagagem.connectbe.services.mappers.MessageMapper;
import com.nabagagem.connectbe.util.UuidHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty("ramifica.web-socket.enabled")
public class WebSocketGateway implements NotificationGateway {
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final MessageMapper messageMapper;
    private final ProfileRepo profileRepo;

    public void send(NotificationCommand notificationCommand, Locale locale) {
        if (notificationCommand.payload() instanceof Message message) {
            sendWsMessage(message, notificationCommand.action(), notificationCommand.profile().getId());
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

    public void sendWsMessage(Message message, Action action, UUID targetProfileId) {
        try {
            log.info("Sending Web socket event to user {}", targetProfileId);
            WsMessage wsMessage = new WsMessage(
                    messageMapper.toDto(message),
                    Optional.ofNullable(UuidHelper.fromString(message.getAudit().getCreatedBy()))
                            .flatMap(profileRepo::getNameFrom)
                            .orElse(null),
                    action);
            simpMessagingTemplate.convertAndSend(
                    "/topics/user/" + targetProfileId,
                    wsMessage);
            log.info("Web socket event sent to user {} with payload {}", targetProfileId, wsMessage);
        } catch (Exception e) {
            log.warn("Failed to send ws message", e);
        }
    }
}
