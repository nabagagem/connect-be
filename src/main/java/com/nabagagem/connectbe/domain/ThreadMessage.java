package com.nabagagem.connectbe.domain;

import java.net.URL;
import java.time.ZonedDateTime;
import java.util.Set;
import java.util.UUID;

public record ThreadMessage(
        UUID id,
        String message,
        String sentBy,
        URL fileUrl,
        ZonedDateTime sentAt,
        Boolean read,
        Set<ThreadMessageReaction> reactions
) {
}
