package com.nabagagem.connectbe.services;

import com.nabagagem.connectbe.domain.JobCategory;
import com.nabagagem.connectbe.domain.JobFrequency;
import com.nabagagem.connectbe.domain.JobMode;
import com.nabagagem.connectbe.domain.JobRequiredAvailability;
import com.nabagagem.connectbe.domain.JobSearchParams;
import com.nabagagem.connectbe.domain.JobSize;
import com.nabagagem.connectbe.entities.Job;
import com.nabagagem.connectbe.repos.JobRepo;
import com.nabagagem.connectbe.services.search.KeywordService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@AllArgsConstructor
public class JobSearchService {
    private final JobRepo jobRepo;
    private final KeywordService keywordService;

    public List<Job> search(JobSearchParams jobSearchParams, UUID loggedUserId, Pageable pageable) {
        Set<String> keywords = Optional.ofNullable(jobSearchParams.searchExpression())
                .map(keywordService::extractFrom)
                .orElse(Set.of());
        List<UUID> ids = jobRepo.findIdsBy(
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
        return jobRepo.findAndFetchByIds(ids);
    }

    private <T> Set<T> emptyOrFull(Set<T> input, T[] values) {
        return Optional.ofNullable(input)
                .filter(ts -> !ts.isEmpty())
                .orElseGet(() -> Set.of(values));
    }
}
