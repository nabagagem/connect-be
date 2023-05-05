package com.nabagagem.connectbe.domain;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record SendMessageCommand(
        @NotBlank UUID senderId,
        @Valid @NotNull @JsonUnwrapped SendMessagePayload sendMessagePayload
) {
}
