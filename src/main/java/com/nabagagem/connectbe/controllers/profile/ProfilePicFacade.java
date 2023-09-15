package com.nabagagem.connectbe.controllers.profile;

import com.nabagagem.connectbe.controllers.MediaControllerHelper;
import com.nabagagem.connectbe.domain.profile.ProfilePicCommand;
import com.nabagagem.connectbe.services.profile.ProfileAuthService;
import com.nabagagem.connectbe.services.profile.ProfilePicService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@AllArgsConstructor
public class ProfilePicFacade {
    private final ProfilePicService profilePicService;
    private final ProfileAuthService profileAuthService;
    private final MediaControllerHelper mediaControllerHelper;

    @CacheEvict(cacheNames = "profile-pic", key = "#profilePicCommand.id()")
    public void save(ProfilePicCommand profilePicCommand) {
        mediaControllerHelper.validateFilePic(profilePicCommand.file());
        profileAuthService.failIfNotLoggedIn(profilePicCommand.id());
        profilePicService.save(profilePicCommand);
    }

    @Cacheable(cacheNames = "profile-pic", key = "#profileId")
    public ResponseEntity<byte[]> getPicFor(UUID profileId) {
        return profilePicService.getPicFor(profileId)
                .map(mediaControllerHelper::toResponse)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
