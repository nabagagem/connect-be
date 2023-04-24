package com.nabagagem.connectbe.controllers;

import com.nabagagem.connectbe.domain.ProfilePicCommand;
import com.nabagagem.connectbe.entities.Media;
import com.nabagagem.connectbe.services.ProfilePicService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@CrossOrigin
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/profile/{id}/pic")
public class ProfilePicController {
    private final ProfilePicService profilePicService;

    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void upload(@RequestParam MultipartFile file,
                       @PathVariable String id) {
        profilePicService.save(new ProfilePicCommand(id, file));
    }

    @GetMapping
    public ResponseEntity<byte[]> get(@PathVariable String id) {
        return profilePicService.getPicFor(id)
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
