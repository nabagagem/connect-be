package com.nabagagem.connectbe.services.profile;

import com.nabagagem.connectbe.repos.ProfileRepo;
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
    private final ProfileService profileService;

    @SneakyThrows
    public void reindex() {
        log.info("Reindexing profiles");
        profileRepo.findAll()
                .forEach(profileService::save);
        log.info("Profiles reindexed");
    }
}
