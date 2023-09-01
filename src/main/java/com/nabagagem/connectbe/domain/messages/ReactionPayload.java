package com.nabagagem.connectbe.domain.messages;

import jakarta.validation.constraints.NotBlank;

public record ReactionPayload(
        @NotBlank @Emoji String reaction
) {
}
