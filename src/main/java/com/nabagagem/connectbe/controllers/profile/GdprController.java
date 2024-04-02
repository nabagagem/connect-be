package com.nabagagem.connectbe.controllers.profile;

import com.nabagagem.connectbe.entities.Gdpr;
import com.nabagagem.connectbe.services.profile.GdprService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@SuppressWarnings("SpringElInspection")
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/profile/{profileId}/gdpr")
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
