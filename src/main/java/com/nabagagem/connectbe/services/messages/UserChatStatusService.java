package com.nabagagem.connectbe.services.messages;

import com.nabagagem.connectbe.domain.messages.UserChatStatusCommand;
import com.nabagagem.connectbe.domain.notification.NotificationCommand;
import com.nabagagem.connectbe.entities.Thread;
import com.nabagagem.connectbe.repos.ThreadRepo;
import com.nabagagem.connectbe.services.notifications.Action;
import com.nabagagem.connectbe.services.notifications.WebSocketGateway;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class UserChatStatusService {
    private final WebSocketGateway webSocketGateway;
    private final ThreadPoolTaskExecutor threadPoolTaskExecutor;
    private final ThreadRepo threadRepo;

    public void update(UUID threadId, UserChatStatusCommand userChatStatusCommand) {
        threadPoolTaskExecutor.submit(() -> {
            try {
                Thread thread = threadRepo.findThreadWithUsers(threadId)
                        .orElseThrow();
                webSocketGateway.send(new NotificationCommand(
                        thread.getRecipient().getId().equals(userChatStatusCommand.sourceUserId()) ? thread.getSender() : thread.getRecipient(),
                        null,
                        threadId.toString(),
                        Action.UPDATED,
                        userChatStatusCommand
                ), LocaleContextHolder.getLocale());
            } catch (Exception e) {
                log.warn("Failed to send chat update message", e);
            }
        });
    }
}
