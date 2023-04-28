package com.nabagagem.connectbe.domain;

import java.util.Set;

public record JobSearchParams(
        Set<JobCategory> jobCategories,
        Set<JobSize> jobSize,
        Set<JobFrequency> jobFrequencies,
        Set<JobMode> jobModes,
        Set<JobRequiredAvailability> requiredAvailabilities,
        Set<String> requiredSkills,
        Set<JobStatus> jobStatuses,
        Set<String> tags
) {
}
