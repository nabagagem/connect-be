package com.nabagagem.connectbe.controllers.profile;

import com.nabagagem.connectbe.domain.ResourceRef;
import com.nabagagem.connectbe.domain.commands.AltProfileCommand;
import com.nabagagem.connectbe.entities.ConnectProfile;
import com.nabagagem.connectbe.services.AltProfileService;
import com.nabagagem.connectbe.services.profile.ProfileAuthService;
import com.nabagagem.connectbe.services.profile.SlugService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/profile/{id}/alts")
public class AltProfileController {
    private final AltProfileService altProfileService;
    private final SlugService slugService;
    private final ProfileAuthService profileAuthService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResourceRef create(@PathVariable String id,
                              @RequestBody @Valid AltProfileCommand altProfileCommand) {
        UUID profileId = slugService.getProfileIdFrom(id);
        profileAuthService.failIfNotCurrentProfile(profileId);
        ConnectProfile profile = altProfileService.create(
                profileId,
                altProfileCommand
        );
        return new ResourceRef(profile.getId().toString());
    }

}
