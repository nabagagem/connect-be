package com.nabagagem.connectbe.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.Set;

public record SkillCommand(@NotBlank String id,
                           @NotEmpty Set<SkillPayload> skills) {
}
