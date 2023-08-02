package com.nabagagem.connectbe.controllers;

import com.nabagagem.connectbe.domain.exceptions.BadRequestException;
import com.nabagagem.connectbe.entities.Media;
import com.nabagagem.connectbe.services.MediaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MediaControllerHelperTest {

    @Mock
    private MediaService mockMediaService;

    private MediaControllerHelper mediaControllerHelperUnderTest;

    @BeforeEach
    void setUp() {
        mediaControllerHelperUnderTest = new MediaControllerHelper(mockMediaService);
    }

    @Test
    void testValidateFile() {
        // Setup
        final MultipartFile file = new MockMultipartFile("name", "name", MediaType.IMAGE_PNG_VALUE, "content".getBytes());

        // Run the test
        mediaControllerHelperUnderTest.validateFilePic(file);

        // Verify the results
    }

    @Test
    void testValidateFile_InvalidFormat() {
        // Setup
        final MultipartFile file = new MockMultipartFile("name", "name", MediaType.TEXT_PLAIN_VALUE, "content".getBytes());

        // Run the test
        assertThatThrownBy(() -> mediaControllerHelperUnderTest.validateFilePic(file))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void testToResponse() {
        // Setup
        final Media media = Media.builder()
                .mediaType(new MediaType("type", "subtype", StandardCharsets.UTF_8))
                .build();
        when(mockMediaService.readFrom(Media.builder()
                .mediaType(new MediaType("type", "subtype", StandardCharsets.UTF_8))
                .build())).thenReturn(Optional.of("content".getBytes()));

        // Run the test
        final ResponseEntity<byte[]> result = mediaControllerHelperUnderTest.toResponse(media);

        // Verify the results
    }

    @Test
    void testToResponse_MediaServiceReturnsAbsent() {
        // Setup
        final Media media = Media.builder()
                .mediaType(new MediaType("type", "subtype", StandardCharsets.UTF_8))
                .build();
        when(mockMediaService.readFrom(Media.builder()
                .mediaType(new MediaType("type", "subtype", StandardCharsets.UTF_8))
                .build())).thenReturn(Optional.empty());

        // Run the test
        final ResponseEntity<byte[]> result = mediaControllerHelperUnderTest.toResponse(media);

        // Verify the results
    }
}
