package com.nabagagem.connectbe.domain.job;

import jakarta.validation.constraints.NotNull;

public record JobPatchPayload(
        @NotNull JobStatus jobStatus
) {
}
