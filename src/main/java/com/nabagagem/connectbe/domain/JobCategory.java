package com.nabagagem.connectbe.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@Getter
@AllArgsConstructor
public enum JobCategory {
    FOOBAR(Set.of()),
    
    FANCY(Set.of(FOOBAR));

    private final Set<JobCategory> subCategories;
}
