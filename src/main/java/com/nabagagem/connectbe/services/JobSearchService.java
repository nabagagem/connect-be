package com.nabagagem.connectbe.services;

import com.nabagagem.connectbe.domain.*;
import com.nabagagem.connectbe.entities.Job;
import com.nabagagem.connectbe.repos.JobRepo;
import com.nabagagem.connectbe.services.search.KeywordService;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@AllArgsConstructor
public class JobSearchService {
    private final JobRepo jobRepo;
    private final KeywordService keywordService;

    public Page<Job> search(JobSearchParams jobSearchParams, UUID loggedUserId, Pageable pageable) {
        Set<String> keywords = Optional.ofNullable(jobSearchParams.searchExpression())
                .map(keywordService::extractFrom)
                .orElse(Set.of());
        Page<UUID> ids = jobRepo.findIdsBy(
                emptyOrFull(jobSearchParams.jobCategories(), JobCategory.values()),
                emptyOrFull(jobSearchParams.jobSize(), JobSize.values()),
                emptyOrFull(jobSearchParams.jobFrequencies(), JobFrequency.values()),
                emptyOrFull(jobSearchParams.jobModes(), JobMode.values()),
                emptyOrFull(jobSearchParams.requiredAvailabilities(), JobRequiredAvailability.values()),
                jobSearchParams.requiredSkills(),
                Optional.ofNullable(jobSearchParams.requiredSkills())
                        .orElseGet(Set::of)
                        .isEmpty(),
                jobSearchParams.owner(),
                jobSearchParams.startAt(),
                jobSearchParams.finishAt(),
                keywords,
                keywords.isEmpty(),
                loggedUserId,
                pageable
        );
        return new PageImpl<>(
                jobRepo.findAndFetchByIds(ids.getContent()),
                pageable,
                ids.getTotalElements()
        );
    }

    private <T> Set<T> emptyOrFull(Set<T> input, T[] values) {
        return Optional.ofNullable(input)
                .filter(ts -> !ts.isEmpty())
                .orElseGet(() -> Set.of(values));
    }

    public Page<Job> search(UUID profileId,
                            ProfileJobSearchParams jobSearchParams,
                            Pageable pageable) {
        Set<String> keywords = Optional.ofNullable(jobSearchParams.expression())
                .filter(StringUtils::isNotBlank)
                .map(keywordService::extractFrom)
                .orElseGet(Set::of);
        Page<UUID> ids = jobRepo.findIdsBy(
                jobSearchParams.jobCategory(),
                jobSearchParams.jobStatus(),
                jobSearchParams.jobMode(),
                keywords,
                keywords.isEmpty(),
                profileId,
                pageable
        );
        return new PageImpl<>(
                jobRepo.findAndFetchByIds(ids.getContent()),
                pageable,
                ids.getTotalElements()
        );
    }
}
