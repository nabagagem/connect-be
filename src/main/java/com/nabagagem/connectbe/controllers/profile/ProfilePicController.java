package com.nabagagem.connectbe.controllers.profile;

import com.nabagagem.connectbe.controllers.MediaPicControllerTrait;
import com.nabagagem.connectbe.domain.ProfilePicCommand;
import com.nabagagem.connectbe.services.ProfilePicService;
import com.nabagagem.connectbe.services.SlugService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/profile/{id}/pic")
public class ProfilePicController implements MediaPicControllerTrait {
    private final ProfilePicService profilePicService;
    private final SlugService slugService;

    @PostAuthorize("#id == authentication.name")
    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void upload(@RequestParam MultipartFile file,
                       @PathVariable String id) {
        validateFile(file);
        profilePicService.save(new ProfilePicCommand(
                slugService.getProfileIdFrom(id),
                file));
    }

    @GetMapping
    public ResponseEntity<byte[]> get(@PathVariable String id) {
        return profilePicService.getPicFor(slugService.getProfileIdFrom(id))
                .map(this::toResponse)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
