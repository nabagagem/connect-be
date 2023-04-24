package com.nabagagem.connectbe.domain;

import java.time.LocalDateTime;

public record ThreadMessage(
        String message,
        String sentBy,
        LocalDateTime sentAt
) {
}
