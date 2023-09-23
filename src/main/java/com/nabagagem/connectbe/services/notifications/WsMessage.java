package com.nabagagem.connectbe.services.notifications;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.nabagagem.connectbe.domain.messages.ThreadMessage;

public record WsMessage(
        @JsonUnwrapped ThreadMessage threadMessage,
        String createdByProfileName,
        Action action) {
}
