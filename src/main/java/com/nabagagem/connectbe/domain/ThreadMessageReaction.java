package com.nabagagem.connectbe.domain;

import java.util.UUID;

public record ThreadMessageReaction(
        UUID id,
        String reaction,
        String createdBy

) {
}
