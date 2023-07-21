package com.nabagagem.connectbe.services.profile;

import com.nabagagem.connectbe.repos.JobRepo;
import com.nabagagem.connectbe.repos.ProfileRepo;
import com.nabagagem.connectbe.services.jobs.JobService;
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
    private final JobRepo jobRepo;
    private final JobService jobService;

    @SneakyThrows
    public void reindex() {
        log.info("Reindexing profiles");
        profileRepo.findAll()
                .forEach(profileService::save);
        log.info("Profiles reindexed");
        log.info("Reindexing jobs");
        jobRepo.findAll()
                .forEach(jobService::save);
        log.info("Jobs reindexed");
    }
}
