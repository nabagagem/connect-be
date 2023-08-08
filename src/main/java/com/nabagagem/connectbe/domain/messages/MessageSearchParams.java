package com.nabagagem.connectbe.domain.messages;

import java.util.UUID;

public record MessageSearchParams(
        String expression,
        UUID messageId,
        Integer behind,
        Integer inFront
) {
}
