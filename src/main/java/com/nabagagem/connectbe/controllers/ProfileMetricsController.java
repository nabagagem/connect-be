package com.nabagagem.connectbe.controllers;

import com.nabagagem.connectbe.domain.ProfileMetrics;
import com.nabagagem.connectbe.services.ProfileMetricsService;
import com.nabagagem.connectbe.services.SlugService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class ProfileMetricsController {
    private final ProfileMetricsService profileMetricsService;
    private final SlugService slugService;

    @GetMapping("/api/v1/profile/{id}/metrics")
    public ProfileMetrics getMetrics(@PathVariable String id) {

        return profileMetricsService.getMetricsFor(slugService.getProfileIdFrom(id));
    }

}
