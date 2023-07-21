package com.nabagagem.connectbe.domain.messages;

import java.util.UUID;

public record ThreadMessageReaction(
        UUID id,
        String reaction,
        String createdBy

) {
}
