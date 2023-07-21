package com.nabagagem.connectbe.domain.messages;

import com.nabagagem.connectbe.entities.ThreadStatus;
import jakarta.validation.constraints.NotNull;

public record PatchThreadPayload(
        @NotNull ThreadStatus status
) {
}
