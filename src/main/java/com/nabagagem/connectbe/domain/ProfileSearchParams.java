package com.nabagagem.connectbe.domain;

import com.nabagagem.connectbe.entities.ProfileCategory;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record ProfileSearchParams(
        Set<ProfileCategory> category,
        Set<WorkingMode> workingMode,
        @Size(max = 100) String searchExpression
) {
}
