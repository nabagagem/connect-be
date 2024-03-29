package com.nabagagem.connectbe.services.jobs;

import com.nabagagem.connectbe.domain.job.JobSearchInfo;
import com.nabagagem.connectbe.domain.job.JobSearchParams;
import com.nabagagem.connectbe.domain.job.ProfileJobSearchParams;
import com.nabagagem.connectbe.entities.Job;
import com.nabagagem.connectbe.repos.JobRepo;
import com.nabagagem.connectbe.services.search.KeywordService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class JobSearchService {
    private final JobRepo jobRepo;
    private final KeywordService keywordService;

    public Page<Job> search(JobSearchParams jobSearchParams, UUID loggedUserId, Pageable pageable) {
        Page<UUID> ids = resolveIdsFrom(jobSearchParams, loggedUserId, pageable);
        return new PageImpl<>(
                jobRepo.findAndFetchByIds(ids.getContent()),
                pageable,
                ids.getTotalElements()
        );
    }

    private Page<UUID> resolveIdsFrom(JobSearchParams jobSearchParams, UUID loggedUserId, Pageable pageable) {
        Set<String> keywords = Optional.ofNullable(jobSearchParams.searchExpression())
                .map(keywordService::extractFrom)
                .orElse(Set.of());
        return jobRepo.findIdsBy(
                jobSearchParams,
                keywords,
                loggedUserId,
                pageable
        );
    }

    public Page<Job> search(UUID profileId,
                            ProfileJobSearchParams jobSearchParams,
                            Pageable pageable) {
        Page<UUID> page = resolveIdsFrom(profileId, jobSearchParams, pageable);
        List<UUID> ids = page.getContent();
        log.info("Found job ids: {}", ids);
        return new PageImpl<>(
                jobRepo.findAndFetchByIds(ids),
                pageable,
                page.getTotalElements()
        );
    }

    private Page<UUID> resolveIdsFrom(UUID profileId, ProfileJobSearchParams jobSearchParams, Pageable pageable) {
        Set<String> keywords = Optional.ofNullable(jobSearchParams.expression())
                .filter(StringUtils::isNotBlank)
                .map(keywordService::extractFrom)
                .orElseGet(Set::of);
        return jobRepo.findIdsBy(
                jobSearchParams.jobCategory(),
                jobSearchParams.jobStatus(),
                jobSearchParams.jobMode(),
                keywords,
                keywords.isEmpty(),
                profileId,
                pageable
        );
    }

    public Page<JobSearchInfo> searchV2(JobSearchParams jobSearchParams,
                                        UUID loggedUserId,
                                        Pageable pageable) {
        Page<UUID> ids = resolveIdsFrom(jobSearchParams, loggedUserId, pageable);
        return new PageImpl<>(
                jobRepo.listJobSearchFrom(ids.getContent(), pageable.getSort()),
                pageable,
                ids.getTotalElements()
        );
    }

    public Page<JobSearchInfo> searchV2(UUID profileId,
                                        ProfileJobSearchParams jobSearchParams,
                                        Pageable pageable) {
        Page<UUID> ids = resolveIdsFrom(profileId, jobSearchParams, pageable);
        return new PageImpl<>(
                jobRepo.listJobSearchFrom(ids.getContent(), pageable.getSort()),
                pageable,
                ids.getTotalElements()
        );
    }
}
