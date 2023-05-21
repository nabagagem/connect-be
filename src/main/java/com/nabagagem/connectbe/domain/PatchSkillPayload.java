package com.nabagagem.connectbe.domain;

import jakarta.validation.constraints.NotNull;

public record PatchSkillPayload(
        @NotNull Boolean top
) {
}
