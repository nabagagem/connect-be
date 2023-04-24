package com.nabagagem.connectbe.domain;

import jakarta.validation.constraints.NotBlank;

public record SendMessagePayload(
        @NotBlank String recipientId,
        @NotBlank String message
) {
}
