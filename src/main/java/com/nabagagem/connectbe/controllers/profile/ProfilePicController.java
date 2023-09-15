package com.nabagagem.connectbe.controllers.profile;

import com.nabagagem.connectbe.domain.profile.ProfilePicCommand;
import com.nabagagem.connectbe.services.profile.SlugService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/profile/{id}/pic")
public class ProfilePicController {
    private final SlugService slugService;
    private final ProfilePicFacade profilePicFacade;

    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void upload(@RequestParam MultipartFile file,
                       @PathVariable String id) {
        UUID profileId = slugService.getProfileIdFrom(id);
        profilePicFacade.save(new ProfilePicCommand(
                profileId,
                file));
    }

    @GetMapping
    public ResponseEntity<byte[]> get(@PathVariable String id) {
        UUID profileId = slugService.getProfileIdFrom(id);
        return profilePicFacade.getPicFor(profileId);
    }
}
