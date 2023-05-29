package com.nabagagem.connectbe.services.notifications;

import com.nabagagem.connectbe.entities.ConnectProfile;
import com.nabagagem.connectbe.repos.ProfileRepo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class LastActivityService {
    private final ProfileRepo profileRepo;

    public void register(ConnectProfile profile) {
        profile.setLastActivity(ZonedDateTime.now());
        profileRepo.save(profile);
    }

    public void register(String id) {
        try {
            profileRepo.findById(UUID.fromString(id))
                    .ifPresent(this::register);
        } catch (IllegalArgumentException e) {
            log.info("Failed to register activity for user: {}", id);
        }
    }
}
