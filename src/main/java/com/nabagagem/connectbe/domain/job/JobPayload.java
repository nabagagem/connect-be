package com.nabagagem.connectbe.domain.job;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nabagagem.connectbe.domain.profile.WorkingMode;
import com.nabagagem.connectbe.entities.DateInterval;
import com.nabagagem.connectbe.entities.MoneyAmount;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Set;
import java.util.UUID;

public record JobPayload(
        @JsonProperty(access = JsonProperty.Access.READ_ONLY) UUID ownerId,
        @JsonProperty(access = JsonProperty.Access.READ_ONLY) UUID id,
        @NotBlank @Size(min = 5, max = 100) String title,
        @Valid MoneyAmount budget,
        JobCategory jobCategory,
        @NotBlank @Size(min = 10, max = 1000) String description,
        @NotNull JobSize jobSize,
        @NotNull JobFrequency jobFrequency,
        @Size(max = 1000) String background,
        @NotNull WorkingMode jobMode,
        JobRequiredAvailability requiredAvailability,
        DateInterval requiredDates,
        @Size(max = 200) String address,
        @Size(max = 200) String addressReference,
        @Size(max = 3) Set<@NotBlank String> requiredSkills,
        JobStatus jobStatus,
        Set<String> tags
) {
}
