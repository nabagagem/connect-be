package com.nabagagem.connectbe.controllers.profile;

import com.nabagagem.connectbe.domain.ResourceRef;
import com.nabagagem.connectbe.domain.profile.ProfileMediaItem;
import com.nabagagem.connectbe.services.profile.ProfileAuthService;
import com.nabagagem.connectbe.services.profile.ProfileMediaService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;
import java.util.UUID;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/profile/{profileId}/media")
public class ProfileMediaController {
    private final ProfileMediaService profileMediaService;
    private final ProfileAuthService profileAuthService;

    @PostMapping
    public ResourceRef upload(@RequestParam MultipartFile file,
                              @PathVariable UUID profileId) {
        profileAuthService.failIfNotLoggedIn(profileId);
        return new ResourceRef(
                profileMediaService.create(profileId, file).getId()
        );
    }

    @GetMapping
    public Set<ProfileMediaItem> list(@PathVariable UUID profileId) {
        profileAuthService.failIfNotLoggedIn(profileId);
        return profileMediaService.listForProfile(profileId);
    }

    @DeleteMapping("/{mediaId}")
    public void delete(@PathVariable UUID profileId,
                       @PathVariable UUID mediaId) {
        profileAuthService.failIfNotLoggedIn(profileId);
        profileMediaService.delete(mediaId);
    }
}
