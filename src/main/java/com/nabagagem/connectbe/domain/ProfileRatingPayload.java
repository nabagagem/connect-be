package com.nabagagem.connectbe.domain;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.UUID;

public record ProfileRatingPayload(
        String sourceProfilePublicName,
        URL sourceProfilePicURL,
        UUID sourceProfileId,
        Integer stars,
        String description,
        LocalDateTime ratedAt
) {
}
