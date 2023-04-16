package com.nabagagem.connectbe.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import static com.nabagagem.connectbe.entities.ProfileSkill.SkillLevel;

public record SkillPayload(@NotBlank String name,
                           @NotNull Integer certifications,
                           @NotNull SkillLevel level) {

}
