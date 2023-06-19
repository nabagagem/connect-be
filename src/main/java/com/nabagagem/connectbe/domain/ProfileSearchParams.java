package com.nabagagem.connectbe.domain;

import jakarta.validation.constraints.Size;

import java.util.Set;

public record ProfileSearchParams(
        Set<JobCategory> category,
        Set<WorkingMode> workingMode,
        @Size(max = 100) String searchExpression
) {
}
