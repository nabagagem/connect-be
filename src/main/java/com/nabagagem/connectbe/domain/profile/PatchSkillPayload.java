package com.nabagagem.connectbe.domain.profile;

import jakarta.validation.constraints.NotNull;

public record PatchSkillPayload(
        @NotNull Boolean top
) {
}
