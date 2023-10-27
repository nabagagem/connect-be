package com.nabagagem.connectbe.controllers.profile;

import com.nabagagem.connectbe.domain.profile.ProfileLinksCommand;
import com.nabagagem.connectbe.entities.LinkType;
import com.nabagagem.connectbe.services.profile.ProfileLinkService;
import lombok.AllArgsConstructor;
import org.hibernate.validator.constraints.URL;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@AllArgsConstructor
public class ProfileLinksController {
    private final ProfileLinkService profileLinkService;

    @PutMapping("/api/v1/profile/{id}/links")
    public void put(@PathVariable UUID id,
                    @RequestBody Map<LinkType, @URL String> links) {
        profileLinkService.update(new ProfileLinksCommand(id, links));
    }

}
