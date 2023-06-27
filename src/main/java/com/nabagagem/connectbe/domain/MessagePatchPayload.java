package com.nabagagem.connectbe.domain;

import jakarta.validation.constraints.Size;

public record MessagePatchPayload(
        @Size(max = 1000) String text,
        Boolean read
) {
}
