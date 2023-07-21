package com.nabagagem.connectbe.domain.rating;

import java.net.URL;
import java.time.ZonedDateTime;
import java.util.UUID;

public record ProfileRatingPayload(
        String sourceProfilePublicName,
        URL sourceProfilePicURL,
        UUID sourceProfileId,
        Integer stars,
        String description,
        ZonedDateTime ratedAt
) {
}
