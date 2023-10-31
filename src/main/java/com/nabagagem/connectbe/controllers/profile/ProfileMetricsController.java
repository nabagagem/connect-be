package com.nabagagem.connectbe.controllers.profile;

import com.nabagagem.connectbe.domain.profile.ProfileMetrics;
import com.nabagagem.connectbe.services.profile.ProfileAuthService;
import com.nabagagem.connectbe.services.profile.ProfileMetricsService;
import com.nabagagem.connectbe.services.profile.SlugService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@AllArgsConstructor
public class ProfileMetricsController {
    private final ProfileMetricsService profileMetricsService;
    private final SlugService slugService;
    private final ProfileAuthService profileAuthService;

    @GetMapping("/api/v1/profile/{id}/metrics")
    public ProfileMetrics getMetrics(@PathVariable String id) {
        UUID profileId = slugService.getProfileIdFrom(id);
        profileAuthService.failIfNotLoggedIn(profileId);
        return profileMetricsService.getMetricsFor(profileId)
                .orElse(new ProfileMetrics(null, null));
    }

}
