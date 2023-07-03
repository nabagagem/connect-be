package com.nabagagem.connectbe.services;

import com.nabagagem.connectbe.domain.JobCategory;
import com.nabagagem.connectbe.domain.JobFrequency;
import com.nabagagem.connectbe.domain.JobMode;
import com.nabagagem.connectbe.domain.JobRequiredAvailability;
import com.nabagagem.connectbe.domain.JobSearchParams;
import com.nabagagem.connectbe.domain.JobSize;
import com.nabagagem.connectbe.entities.Job;
import com.nabagagem.connectbe.repos.JobRepo;
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

    public List<Job> search(JobSearchParams jobSearchParams, UUID loggedUserId, Pageable pageable) {
        List<UUID> ids = jobRepo.findIdsBy(
                emptyOrFull(jobSearchParams.jobCategories(), JobCategory.values()),
                emptyOrFull(jobSearchParams.jobSize(), JobSize.values()),
                emptyOrFull(jobSearchParams.jobFrequencies(), JobFrequency.values()),
                emptyOrFull(jobSearchParams.jobModes(), JobMode.values()),
                emptyOrFull(jobSearchParams.requiredAvailabilities(), JobRequiredAvailability.values()),
                jobSearchParams.requiredSkills(),
                jobSearchParams.tags(),
                jobSearchParams.owner(),
                jobSearchParams.startAt(),
                jobSearchParams.finishAt(),
                jobSearchParams.searchExpression(),
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
