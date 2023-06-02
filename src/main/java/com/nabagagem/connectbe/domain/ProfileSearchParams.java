package com.nabagagem.connectbe.domain;

import com.nabagagem.connectbe.entities.ProfileCategory;

import java.util.Set;

public record ProfileSearchParams(
        Set<ProfileCategory> category,
        Set<WorkingMode> workingMode,
        String searchExpression
) {
}
