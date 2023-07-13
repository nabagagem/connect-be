package com.nabagagem.connectbe.services.profile;

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

    public void register(UUID id) {
        profileRepo.updateLastActivityFor(id, ZonedDateTime.now());
    }
}
