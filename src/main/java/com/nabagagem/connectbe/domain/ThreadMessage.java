package com.nabagagem.connectbe.domain;

import org.springframework.http.MediaType;

import java.net.URL;
import java.time.ZonedDateTime;
import java.util.Set;
import java.util.UUID;

public record ThreadMessage(
        UUID id,
        String message,
        String sentBy,
        URL fileUrl,
        MediaType mediaType,
        String mediaOriginalName,
        ZonedDateTime sentAt,
        Boolean read,
        Set<ThreadMessageReaction> reactions
) {
}
