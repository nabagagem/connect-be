package com.nabagagem.connectbe.domain.messages;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Value;

import java.util.UUID;

@Value
public class SendMessagePayload {
    UUID recipientId;
    UUID bidId;
    @NotBlank @Size(max = 1000) String message;
}
