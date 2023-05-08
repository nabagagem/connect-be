package com.nabagagem.connectbe.domain;

import jakarta.validation.constraints.NotBlank;
import lombok.Value;

import java.util.UUID;

@Value
public class SendMessagePayload {
    UUID recipientId;
    UUID bidId;
    @NotBlank String message;
}
