package com.nabagagem.connectbe.domain;

import java.time.ZonedDateTime;

public record ThreadMessage(
        String message,
        String sentBy,
        ZonedDateTime sentAt
) {
}
