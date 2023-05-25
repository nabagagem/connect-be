package com.nabagagem.connectbe.services.notifications;

import com.nabagagem.connectbe.domain.NotificationCommand;
import com.nabagagem.connectbe.domain.UpdateNotifCommand;
import com.nabagagem.connectbe.entities.Notification;
import com.nabagagem.connectbe.repos.NotificationRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class NotificationService {
    private final NotificationMapper notificationMapper;
    private final NotificationRepository notificationRepository;
    private final MessageGateway messageGateway;

    public NotificationService(NotificationMapper notificationMapper,
                               NotificationRepository notificationRepository,
                               @Autowired(required = false) MessageGateway messageGateway) {
        this.notificationMapper = notificationMapper;
        this.notificationRepository = notificationRepository;
        this.messageGateway = messageGateway;
    }

    public void create(@Valid NotificationCommand notificationCommand) {
        log.info("Creating Notification: {}", notificationCommand);
        notificationRepository.save(
                notificationMapper.toEntity(notificationCommand)
        );
        Optional.ofNullable(messageGateway)
                .ifPresent(__ -> messageGateway.send(notificationCommand));
    }

    public List<Notification> list(UUID userId) {
        return notificationRepository.findByTargetProfileId(userId);
    }

    public void update(@Valid UpdateNotifCommand updateNotifCommand) {
        notificationRepository.update(updateNotifCommand.notificationId(),
                updateNotifCommand.statusPayload().read());
    }
}
