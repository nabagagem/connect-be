package com.nabagagem.connectbe.domain;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SendMessageCommand(
        @NotBlank String senderId,
        @Valid @NotNull @JsonUnwrapped SendMessagePayload sendMessagePayload
) {
}
