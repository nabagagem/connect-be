package com.nabagagem.connectbe.services.notifications;

import com.nabagagem.connectbe.domain.notification.NotificationCommand;
import com.nabagagem.connectbe.domain.notification.UpdateNotifCommand;
import com.nabagagem.connectbe.entities.Notification;
import com.nabagagem.connectbe.repos.NotificationRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class NotificationService {
    private final NotificationMapper notificationMapper;
    private final NotificationRepository notificationRepository;
    private final List<NotificationGateway> notificationGateways;
    private final ThreadPoolTaskExecutor threadPoolTaskExecutor;

    public void create(@Valid NotificationCommand notificationCommand) {
        log.info("Persisting Notification {} for user {}", notificationCommand.targetObjectId(),
                notificationCommand.profile().getId());
        notificationRepository.save(
                notificationMapper.toEntity(notificationCommand)
        );
        Locale locale = LocaleContextHolder.getLocale();
        notificationGateways
                .forEach(gateway -> threadPoolTaskExecutor
                        .submit(() -> gateway.send(notificationCommand, locale)));
    }

    public List<Notification> list(UUID userId) {
        return notificationRepository.findByTargetProfileId(userId);
    }

    public void update(@Valid UpdateNotifCommand updateNotifCommand) {
        notificationRepository.update(updateNotifCommand.notificationId(),
                updateNotifCommand.statusPayload().read());
    }
}
