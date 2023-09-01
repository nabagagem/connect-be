package com.nabagagem.connectbe.services.notifications;

import com.nabagagem.connectbe.domain.notification.EventNotification;
import com.nabagagem.connectbe.entities.ConnectProfile;
import com.nabagagem.connectbe.entities.Reaction;
import com.nabagagem.connectbe.entities.Thread;
import com.nabagagem.connectbe.repos.MessageRepo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@AllArgsConstructor
public class ReactionNotificationListener {
    private final WebSocketGateway webSocketGateway;
    private final MessageRepo messageRepo;
    private final ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void afterCommit(EventNotification notification) {
        if (notification.result() instanceof Reaction reaction) {
            handleReaction(reaction);
        }
    }

    private void handleReaction(Reaction reaction) {
        threadPoolTaskExecutor.submit(() -> send(reaction));
    }

    private void send(Reaction reaction) {
        try {
            messageRepo.findWithThread(reaction.getMessage().getId())
                    .ifPresent(message -> webSocketGateway.sendWsMessage(
                            message,
                            Action.UPDATED,
                            resolveTargetFrom(
                                    message.getThread(),
                                    reaction.getAudit().getCreatedBy())
                                    .getId()));
        } catch (Exception e) {
            log.info("Failed to send reaction ws notification", e);
        }
    }

    private ConnectProfile resolveTargetFrom(Thread thread, String createdBy) {
        ConnectProfile recipient = thread.getRecipient();
        return recipient.getId().toString().equals(createdBy)
                ? thread.getSender() : recipient;
    }

}
