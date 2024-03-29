package com.nabagagem.connectbe.domain.job;

import com.nabagagem.connectbe.domain.profile.WorkingMode;
import com.nabagagem.connectbe.entities.DateInterval;
import com.nabagagem.connectbe.entities.MoneyAmount;

import java.time.ZonedDateTime;
import java.util.Set;
import java.util.UUID;

public record JobSearchItem(
        UUID id,
        ProfileJobItem profile,
        String title,
        MoneyAmount budget,
        JobCategory jobCategory,
        String description,
        JobSize jobSize,
        JobFrequency jobFrequency,
        String background,
        WorkingMode jobMode,
        JobRequiredAvailability requiredAvailability,
        DateInterval dateInterval,
        String address,
        String addressReference,
        Set<String> requiredSkills,
        JobStatus jobStatus,
        Set<String> tags,
        DateInterval requiredDates,
        ZonedDateTime createdAt
) {

}
