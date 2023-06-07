package com.nabagagem.connectbe.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@Getter
@AllArgsConstructor
public enum JobCategory {
    WEB_DEV(false, Set.of()),
    IT(true, Set.of(WEB_DEV)),
    DESIGN_MULTIMEDIA(true, Set.of()),
    TRANSLATIONS_CONTENT(true, Set.of()),
    MARKETING_SALES(true, Set.of()),
    BEAUTY_CARE(true, Set.of()),
    MGMT_ADVICE(true, Set.of()),
    ARTS_CRAFTSMANSHIP(true, Set.of()),
    PET_SITTER(true, Set.of()),
    PET_CARE(true, Set.of()),
    BABY_SITTER(true, Set.of()),
    HAUS_KEEPING(true, Set.of()),
    GARDENING(true, Set.of()),
    OVERALL_MAINTENANCE(true, Set.of());

    private final Boolean root;
    private final Set<JobCategory> subCategories;
}
