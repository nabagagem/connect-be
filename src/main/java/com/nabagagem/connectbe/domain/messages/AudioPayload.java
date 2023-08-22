package com.nabagagem.connectbe.domain.messages;

import jakarta.validation.constraints.NotBlank;

public record AudioPayload(
        @NotBlank String content
) {
}
