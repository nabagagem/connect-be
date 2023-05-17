package com.nabagagem.connectbe.services.notifications;

import com.nabagagem.connectbe.domain.NotificationCommand;
import com.nabagagem.connectbe.domain.UpdateNotifCommand;
import com.nabagagem.connectbe.entities.Notification;
import com.nabagagem.connectbe.repos.NotificationRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class NotificationService {
    private final NotificationMapper notificationMapper;
    private final NotificationRepository notificationRepository;
    private final MessageGateway messageGateway;

    public void create(@Valid NotificationCommand notificationCommand) {
        log.info("Creating Notification: {}", notificationCommand);
        notificationRepository.save(
                notificationMapper.toEntity(notificationCommand)
        );
        messageGateway.send(notificationCommand);
    }

    public List<Notification> list(UUID userId) {
        return notificationRepository.findByTargetProfileId(userId);
    }

    public void update(@Valid UpdateNotifCommand updateNotifCommand) {
        notificationRepository.update(updateNotifCommand.notificationId(),
                updateNotifCommand.statusPayload().read());
    }
}
