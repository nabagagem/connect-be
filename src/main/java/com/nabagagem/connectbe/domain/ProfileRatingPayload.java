package com.nabagagem.connectbe.domain;

import java.time.LocalDateTime;
import java.util.UUID;

public record ProfileRatingPayload(
        String sourceProfilePublicName,
        UUID sourceProfileId,
        Integer stars,
        String description,
        LocalDateTime ratedAt
) {
}
