package com.nabagagem.connectbe.domain.messages;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ReactionPayload(
        @NotBlank @Size(max = 10) String reaction
) {
}
