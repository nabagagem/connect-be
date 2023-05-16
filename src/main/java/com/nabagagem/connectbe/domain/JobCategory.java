package com.nabagagem.connectbe.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@Getter
@AllArgsConstructor
public enum JobCategory {
    WEB_DEV(Set.of()),
    IT(Set.of(WEB_DEV)),
    DESIGN_MULTIMEDIA(Set.of()),
    TRANSLATIONS_CONTENT(Set.of()),
    MARKETING_SALES(Set.of()),
    MGMT_ADVICE(Set.of()),
    ARTS_CRAFTSMANSHIP(Set.of()),
    PET_CARE(Set.of()),
    BABY_SITTER(Set.of()),
    HAUS_KEEPING(Set.of()),
    GARDENING(Set.of()),
    OVERALL_MAINTENANCE(Set.of());

    private final Set<JobCategory> subCategories;
}
