package com.nabagagem.connectbe.services.notifications;

import com.nabagagem.connectbe.domain.NotificationPayload;
import com.nabagagem.connectbe.entities.Bid;
import com.nabagagem.connectbe.entities.Message;
import com.nabagagem.connectbe.entities.NotificationType;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@AllArgsConstructor
@Transactional(Transactional.TxType.REQUIRES_NEW)
public class EntityNotificationListener {
    private final NotificationService notificationService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void afterCreate(Message message) {
        notificationService.create(
                new NotificationPayload(message.getThread().getRecipient(),
                        message.getText(),
                        message.getId().toString(),
                        NotificationType.NEW_MESSAGE)
        );
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void afterCreate(Bid bid) {
        notificationService.create(
                new NotificationPayload(
                        bid.getTargetJob().getOwner(),
                        bid.getHighlights(),
                        bid.getId().toString(),
                        NotificationType.NEW_BID
                )
        );
    }
}
