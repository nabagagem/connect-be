package com.nabagagem.connectbe.domain;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record SendMessagePayload(
        UUID recipientId,
        UUID bidId,
        @NotBlank String message
) {
}
