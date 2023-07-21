package com.nabagagem.connectbe.domain.messages;

import java.util.UUID;

public record ThreadMessageCommand(UUID threadId, TextPayload textPayload) {
}

