package com.nabagagem.connectbe.controllers;

import com.nabagagem.connectbe.domain.ProfilePicCommand;
import com.nabagagem.connectbe.domain.exceptions.BadRequestException;
import com.nabagagem.connectbe.domain.exceptions.ErrorType;
import com.nabagagem.connectbe.entities.Media;
import com.nabagagem.connectbe.services.ProfilePicService;
import com.nabagagem.connectbe.services.SlugService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/profile/{id}/pic")
public class ProfilePicController {
    private final ProfilePicService profilePicService;
    private final SlugService slugService;
    private final Set<String> validTypes = Set.of(
            MediaType.IMAGE_PNG_VALUE,
            MediaType.IMAGE_JPEG_VALUE);

    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void upload(@RequestParam MultipartFile file,
                       @PathVariable String id) {
        validateFile(file);
        profilePicService.save(new ProfilePicCommand(
                slugService.getProfileIdFrom(id),
                file));
    }

    private void validateFile(MultipartFile file) {
        if (!validTypes.contains(file.getContentType())) {
            throw BadRequestException.builder()
                    .errorType(ErrorType.INVALID_PROFILE_PIC_FORMAT)
                    .build();
        }
    }

    @GetMapping
    public ResponseEntity<byte[]> get(@PathVariable String id) {
        return profilePicService.getPicFor(slugService.getProfileIdFrom(id))
                .map(this::toResponse)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private ResponseEntity<byte[]> toResponse(Media media) {
        byte[] body = media.getFileContent();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(media.getMediaType());
        headers.setContentLength(body.length);
        return ResponseEntity.status(HttpStatus.OK)
                .headers(headers)
                .body(body);
    }
}
