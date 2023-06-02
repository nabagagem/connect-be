package com.nabagagem.connectbe.services.notifications;

import com.nabagagem.connectbe.repos.ProfileRepo;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class LastActivityService {
    private final ProfileRepo profileRepo;

    public void register(String id) {
        try {
            profileRepo.updateLastActivityFor(UUID.fromString(id), ZonedDateTime.now());
        } catch (IllegalArgumentException e) {
            log.info("Failed to register activity for user: {}", id);
        }
    }
}
