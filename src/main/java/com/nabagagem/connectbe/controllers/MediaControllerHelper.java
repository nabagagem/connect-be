package com.nabagagem.connectbe.controllers;

import com.nabagagem.connectbe.domain.exceptions.BadRequestException;
import com.nabagagem.connectbe.domain.exceptions.ErrorType;
import com.nabagagem.connectbe.entities.Media;
import com.nabagagem.connectbe.services.MediaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class MediaControllerHelper {
    private final MediaService mediaService;
    private final Set<String> validPicTypes = Set.of(
            MediaType.IMAGE_PNG_VALUE,
            MediaType.IMAGE_JPEG_VALUE);

    public void validateFilePic(MultipartFile file) {
        Optional.ofNullable(file.getContentType())
                .filter(validPicTypes::contains)
                .ifPresentOrElse(__ -> {
                }, () -> {
                    throw BadRequestException.builder()
                            .errorType(ErrorType.INVALID_PROFILE_PIC_FORMAT)
                            .build();
                });
    }

    public ResponseEntity<byte[]> toResponse(Media media) {
        return mediaService.readFrom(media)
                .map(body -> {
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(media.getMediaType());
                    headers.setContentLength(body.length);
                    Optional.ofNullable(media.getOriginalName())
                            .ifPresent(originalFileName -> {
                                headers.setContentDisposition(ContentDisposition.attachment()
                                        .filename(originalFileName)
                                        .name(originalFileName)
                                        .build());
                            });
                    return ResponseEntity.status(HttpStatus.OK)
                            .headers(headers)
                            .body(body);

                }).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
