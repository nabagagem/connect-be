package com.nabagagem.connectbe.services.notifications;

import com.nabagagem.connectbe.entities.ConnectProfile;
import com.nabagagem.connectbe.repos.ProfileRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
public class LastActivityService {
    private final ProfileRepo profileRepo;

    public void register(ConnectProfile profile) {
        profile.setLastActivity(LocalDateTime.now());
        profileRepo.save(profile);
    }

    public void register(String id) {
        profileRepo.findById(UUID.fromString(id))
                .ifPresent(this::register);
    }
}
