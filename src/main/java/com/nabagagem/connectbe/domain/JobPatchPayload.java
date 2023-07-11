package com.nabagagem.connectbe.domain;

import jakarta.validation.constraints.NotNull;

public record JobPatchPayload(
        @NotNull JobStatus jobStatus
) {
}
