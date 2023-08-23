package com.nabagagem.connectbe.domain.messages;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateAudioCommand(
        @NotNull UUID threadId, @NotNull @Valid AudioPayload audioPayload) {
}
