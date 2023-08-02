package com.nabagagem.connectbe.services.notifications;

import com.nabagagem.connectbe.domain.notification.EventNotification;
import com.nabagagem.connectbe.domain.notification.NotificationCommand;
import com.nabagagem.connectbe.entities.ConnectProfile;
import com.nabagagem.connectbe.entities.Message;
import com.nabagagem.connectbe.entities.NotificationType;
import com.nabagagem.connectbe.entities.Thread;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@AllArgsConstructor
@Transactional(Transactional.TxType.REQUIRES_NEW)
public class MessageNotificationListener {
    private final NotificationService notificationService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void afterCommit(EventNotification notification) {
        log.info("Publishing notification: {} {}", notification.result().getClass().getSimpleName(),
                notification.notification().value());
        if (notification.result() instanceof Message message) {
            log.info("Sending message notification: {}", message.getText());
            afterCommit(message, notification.notification().value());
            return;
        }
        if (notification.result() instanceof Thread thread) {
            log.info("Sending message notification: {}", thread.getId());
            afterCommit(thread, notification.notification().value());
        }
    }

    public void afterCommit(Message message, PublishNotification.Action action) {
        Thread thread = message.getThread();
        ConnectProfile profile = resolveTargetFrom(message, thread);
        log.info("Publishing ws event to user {}", profile.getId());
        notificationService.create(
                new NotificationCommand(
                        profile,
                        message.getText(),
                        thread.getId().toString(),
                        toMessageNotificationType(action),
                        message
                )
        );
    }

    private ConnectProfile resolveTargetFrom(Message message, Thread thread) {
        return thread.getRecipient().getId()
                .toString().equals(message.getAudit().getModifiedBy())
                ? thread.getSender() : thread.getRecipient();

    }

    private NotificationType toMessageNotificationType(PublishNotification.Action action) {
        return switch (action) {
            case CREATED -> NotificationType.NEW_MESSAGE;
            case UPDATED -> NotificationType.UPDATED_MESSAGE;
            case DELETED -> NotificationType.DELETED_MESSAGE;
        };
    }

    public void afterCommit(Thread thread, PublishNotification.Action action) {
        if (action == PublishNotification.Action.CREATED) {
            Message message = thread.getLastMessage();
            afterCommit(message, action);
        }
    }
}
