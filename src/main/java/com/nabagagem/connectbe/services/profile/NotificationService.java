package com.nabagagem.connectbe.services.profile;

import com.nabagagem.connectbe.repos.NotificationRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;

    public void deleteForUser(UUID id) {
        notificationRepository.findByTargetProfileId(id)
                .forEach(notificationRepository::delete);
    }
}
