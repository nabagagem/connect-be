package com.nabagagem.connectbe.controllers.profile;

import com.nabagagem.connectbe.entities.Gdpr;
import com.nabagagem.connectbe.services.profile.GdprService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@SuppressWarnings("SpringElInspection")
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/profile/{profileId}/gdpr")
@PreAuthorize("authentication.name == #profileId")
public class GdprController {
    private final GdprService gdprService;

    @PutMapping
    public void update(@PathVariable String profileId,
                       @RequestBody @Valid Gdpr gdpr) {
        gdprService.update(UUID.fromString(profileId), gdpr);
    }

    @GetMapping
    public Gdpr get(@PathVariable String profileId) {
        return new Gdpr(gdprService.get(UUID.fromString(profileId)));
    }
}
