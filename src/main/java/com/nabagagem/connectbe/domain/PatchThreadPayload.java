package com.nabagagem.connectbe.domain;

import com.nabagagem.connectbe.entities.ThreadStatus;
import jakarta.validation.constraints.NotNull;

public record PatchThreadPayload(
        @NotNull ThreadStatus status
) {
}
