package com.nabagagem.connectbe.domain.messages;

import jakarta.validation.constraints.Size;

public record MessagePatchPayload(
        @Size(max = 1000) String text,
        Boolean read
) {
}
