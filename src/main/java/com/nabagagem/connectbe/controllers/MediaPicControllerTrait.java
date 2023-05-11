package com.nabagagem.connectbe.controllers;

import com.nabagagem.connectbe.domain.exceptions.BadRequestException;
import com.nabagagem.connectbe.domain.exceptions.ErrorType;
import com.nabagagem.connectbe.entities.Media;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

public interface MediaPicControllerTrait {
    Set<String> validTypes = Set.of(
            MediaType.IMAGE_PNG_VALUE,
            MediaType.IMAGE_JPEG_VALUE);

    default void validateFile(MultipartFile file) {
        if (!validTypes.contains(file.getContentType())) {
            throw BadRequestException.builder()
                    .errorType(ErrorType.INVALID_PROFILE_PIC_FORMAT)
                    .build();
        }
    }

    default ResponseEntity<byte[]> toResponse(Media media) {
        byte[] body = media.getFileContent();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(media.getMediaType());
        headers.setContentLength(body.length);
        return ResponseEntity.status(HttpStatus.OK)
                .headers(headers)
                .body(body);
    }
}
