package com.nabagagem.connectbe.services;

import com.nabagagem.connectbe.domain.*;
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

    public List<Job> search(JobSearchParams jobSearchParams, Pageable pageable) {
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
