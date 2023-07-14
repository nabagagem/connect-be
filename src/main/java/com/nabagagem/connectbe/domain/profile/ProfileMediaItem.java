package com.nabagagem.connectbe.domain.profile;

import org.springframework.http.MediaType;

import java.util.UUID;

public record ProfileMediaItem(
        UUID id,
        MediaType mediaType,
        String originalName
) {
}
