package com.nabagagem.connectbe.domain;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

import java.util.UUID;

@Value
public class SendMessageCommand {
    @NotNull UUID senderId;
    @JsonUnwrapped
    @Valid @NotNull SendMessagePayload sendMessagePayload;
}
