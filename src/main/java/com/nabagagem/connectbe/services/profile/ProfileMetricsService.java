package com.nabagagem.connectbe.services.profile;

import com.nabagagem.connectbe.domain.profile.ProfileMetrics;
import com.nabagagem.connectbe.entities.ConnectProfile;
import com.nabagagem.connectbe.repos.JobRepo;
import com.nabagagem.connectbe.repos.ProfileRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ProfileMetricsService {
    private final ProfileRepo profileRepo;
    private final JobRepo jobRepo;

    public Optional<ProfileMetrics> getMetricsFor(ConnectProfile profile) {
        return Optional.of(new ProfileMetrics(
                profile.getLastActivity(),
                profile.getAudit().getCreatedAt()));
    }

    public Optional<ProfileMetrics> getMetricsFor(UUID profileId) {
        return profileRepo.findById(profileId)
                .flatMap(this::getMetricsFor);
    }
}
