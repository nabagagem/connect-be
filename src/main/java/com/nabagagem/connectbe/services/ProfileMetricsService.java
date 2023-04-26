package com.nabagagem.connectbe.services;

import com.nabagagem.connectbe.domain.ProfileMetrics;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ProfileMetricsService {
    public ProfileMetrics getMetricsFor(String id) {
        return new ProfileMetrics(
                48,
                8,
                96,
                48,
                0,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusYears(1)
        );
    }
}
