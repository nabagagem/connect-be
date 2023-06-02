package com.nabagagem.connectbe.domain;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import static com.nabagagem.connectbe.entities.ProfileSkill.SkillLevel;

public record SkillPayload(@NotBlank String name,
                           @NotNull @Min(0) Integer certifications,
                           @NotNull SkillLevel level,
                           @NotNull Boolean top) {

}
