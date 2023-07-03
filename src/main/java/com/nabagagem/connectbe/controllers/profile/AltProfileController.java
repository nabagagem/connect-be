package com.nabagagem.connectbe.controllers.profile;

import com.nabagagem.connectbe.domain.ResourceRef;
import com.nabagagem.connectbe.domain.commands.AltProfileCommand;
import com.nabagagem.connectbe.entities.ConnectProfile;
import com.nabagagem.connectbe.services.AltProfileService;
import com.nabagagem.connectbe.services.profile.SlugService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/profile/{id}/alternative")
public class AltProfileController {
    private final AltProfileService altProfileService;
    private final SlugService slugService;

    @PostMapping
    public ResourceRef create(@PathVariable String id,
                              @RequestBody @Valid AltProfileCommand altProfileCommand) {
        ConnectProfile profile = altProfileService.create(
                slugService.getProfileIdFrom(id),
                altProfileCommand
        );
        return new ResourceRef(profile.getId().toString());
    }

}
