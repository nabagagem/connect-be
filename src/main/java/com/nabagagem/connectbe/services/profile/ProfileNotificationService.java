package com.nabagagem.connectbe.services.profile;

import com.nabagagem.connectbe.entities.ConnectProfile;
import com.nabagagem.connectbe.entities.NotificationSettings;
import com.nabagagem.connectbe.repos.ProfileRepo;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional
public class ProfileNotificationService {
    private final ProfileService profileService;
    private final ProfileRepo profileRepo;

    public void update(UUID id, NotificationSettings notificationSettings) {
        profileRepo.findById(id)
                .map(profile -> {
                    profile.setNotificationSettings(notificationSettings);
                    return profile;
                }).ifPresent(profileService::save);
    }

    public Optional<NotificationSettings> getSettings(UUID id) {
        return profileRepo.findById(id)
                .map(ConnectProfile::getNotificationSettings);
    }
}
