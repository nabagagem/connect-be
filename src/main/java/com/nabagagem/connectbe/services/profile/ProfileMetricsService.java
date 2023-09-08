package com.nabagagem.connectbe.services.profile;

import com.nabagagem.connectbe.domain.profile.ProfileMetrics;
import com.nabagagem.connectbe.repos.ProfileRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ProfileMetricsService {
    private final ProfileRepo profileRepo;

    public Optional<ProfileMetrics> getMetricsFor(UUID id) {
        return profileRepo.findById(id)
                .map(connectProfile -> new ProfileMetrics(
                        0L, 0L, 0L, 0L, 0L,
                        connectProfile.getLastActivity(),
                        connectProfile.getAudit().getCreatedAt()));
    }
}
