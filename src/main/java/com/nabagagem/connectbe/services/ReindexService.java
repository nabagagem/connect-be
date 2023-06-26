package com.nabagagem.connectbe.services;

import com.nabagagem.connectbe.repos.ProfileRepo;
import com.nabagagem.connectbe.services.profile.ProfileIndexingService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
@Transactional
public class ReindexService {
    private final ProfileRepo profileRepo;
    private final ProfileIndexingService profileIndexingService;

    @SneakyThrows
    public void reindex() {
        log.info("Reindexing profiles");
        profileRepo.findAll()
                .forEach(profileIndexingService::index);
        log.info("Profiles reindexed");
    }
}
