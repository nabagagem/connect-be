package com.nabagagem.connectbe.controllers.job;

import com.nabagagem.connectbe.domain.JobSearchItem;
import com.nabagagem.connectbe.domain.JobSearchParams;
import com.nabagagem.connectbe.domain.ProfileJobSearchParams;
import com.nabagagem.connectbe.services.JobMapper;
import com.nabagagem.connectbe.services.JobSearchService;
import com.nabagagem.connectbe.services.profile.ProfileAuthService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.UUID;

@RestController
@AllArgsConstructor
@Slf4j
public class JobSearchController {
    private final JobSearchService jobSearchService;
    private final JobMapper jobMapper;
    private final ProfileAuthService profileAuthService;

    @GetMapping("/api/v1/jobs")
    public Page<JobSearchItem> get(JobSearchParams jobSearchParams,
                                   Pageable pageable,
                                   Principal principal) {
        log.info("Search params: {}", jobSearchParams);
        return jobSearchService.search(jobSearchParams,
                        UUID.fromString(principal.getName()),
                        pageable)
                .map(jobMapper::toSearchItem);
    }

    @GetMapping("/api/v1/profile/{profileId}/jobs")
    public Page<JobSearchItem> getByProfile(ProfileJobSearchParams jobSearchParams,
                                            @PathVariable UUID profileId,
                                            Pageable pageable) {
        profileAuthService.failIfNotLoggedIn(profileId);
        log.info("Search params: {}", jobSearchParams);
        return jobSearchService.search(profileId, jobSearchParams, pageable)
                .map(jobMapper::toSearchItem);
    }

}
