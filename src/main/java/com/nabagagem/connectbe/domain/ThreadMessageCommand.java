package com.nabagagem.connectbe.domain;

import java.util.UUID;

public record ThreadMessageCommand(UUID threadId, TextPayload textPayload) {
}

