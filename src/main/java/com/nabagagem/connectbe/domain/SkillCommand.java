package com.nabagagem.connectbe.domain;

import jakarta.validation.constraints.NotEmpty;

import java.util.Set;

public record SkillCommand(java.util.UUID id,
                           @NotEmpty Set<SkillPayload> skills) {
}
