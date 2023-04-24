package com.nabagagem.connectbe.domain;

public record SendMessageCommand(
        String senderId,
        SendMessagePayload sendMessagePayload
) {
}
