package com.nabagagem.connectbe.services.notifications;

import com.nabagagem.connectbe.domain.notification.EventNotification;
import com.nabagagem.connectbe.entities.ConnectProfile;
import com.nabagagem.connectbe.entities.Message;
import com.nabagagem.connectbe.entities.Thread;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@AllArgsConstructor
public class MessageNotificationListener {
    private final WebSocketGateway webSocketGateway;
    private final ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void afterCommit(EventNotification notification) {
        threadPoolTaskExecutor.submit(() -> handleMessage(notification));
    }

    private void handleMessage(EventNotification notification) {
        log.info("Publishing notification: {} {}", notification.result().getClass().getSimpleName(),
                notification.notification().value());
        if (notification.result() instanceof Message message) {
            log.info("Sending message notification: {}", message.getText());
            handleMessage(message, notification.notification().value());
            return;
        }
        if (notification.result() instanceof Thread thread) {
            log.info("Sending message notification: {}", thread.getId());
            handleMessage(thread, notification.notification().value());
        }
    }

    public void handleMessage(Message message, Action action) {
        Thread thread = message.getThread();
        ConnectProfile profile = resolveTargetFrom(message, thread, action);
        log.info("Publishing ws event to user {}", profile.getId());
        webSocketGateway.sendWsMessage(message, action, resolveTargetFrom(message, thread, action).getId());
    }

    private ConnectProfile resolveTargetFrom(Message message, Thread thread, Action action) {
        return thread.getRecipient().getId()
                .toString().equals(
                        action == Action.DELETED ? message.getAudit().getCreatedBy() : message.getAudit().getModifiedBy()
                )
                ? thread.getSender() : thread.getRecipient();

    }

    public void handleMessage(Thread thread, Action action) {
        if (action == Action.CREATED) {
            Message message = thread.getLastMessage();
            handleMessage(message, action);
        }
    }
}
