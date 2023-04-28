package com.nabagagem.connectbe.domain;

import com.nabagagem.connectbe.entities.MoneyAmount;

import java.util.Set;

public record JobSearchItem(
        ProfileSearchItem profile,
        String title,
        MoneyAmount budget,
        JobCategory jobCategory,
        String description,
        JobSize jobSize,
        JobFrequency jobFrequency,
        String background,
        JobMode jobMode,
        JobRequiredAvailability requiredAvailability,
        String address,
        String addressReference,
        Set<String> requiredSkills,
        JobStatus jobStatus,
        Set<String> tags
) {

}
