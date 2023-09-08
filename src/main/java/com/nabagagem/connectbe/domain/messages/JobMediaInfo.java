package com.nabagagem.connectbe.domain.messages;

import com.nabagagem.connectbe.domain.FilePurpose;
import org.springframework.http.MediaType;

import java.util.UUID;

/**
 * Projection for {@link com.nabagagem.connectbe.entities.JobMedia}
 */
public interface JobMediaInfo {
    UUID getId();

    FilePurpose getFilePurpose();

    Integer getPosition();

    MediaInfo getMedia();

    /**
     * Projection for {@link com.nabagagem.connectbe.entities.Media}
     */
    interface MediaInfo {
        String getOriginalName();

        MediaType getMediaType();

        String getDescription();
    }
}