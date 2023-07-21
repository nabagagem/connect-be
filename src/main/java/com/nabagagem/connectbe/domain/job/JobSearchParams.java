package com.nabagagem.connectbe.domain.job;

import java.time.ZonedDateTime;
import java.util.Set;
import java.util.UUID;

public record JobSearchParams(
        Set<JobCategory> jobCategories,
        Set<JobSize> jobSize,
        Set<JobFrequency> jobFrequencies,
        Set<JobMode> jobModes,
        Set<JobRequiredAvailability> requiredAvailabilities,
        Set<String> requiredSkills,
        Set<JobStatus> jobStatuses,
        Set<String> tags,
        UUID owner,
        ZonedDateTime startAt,
        ZonedDateTime finishAt,
        String searchExpression
) {
}
