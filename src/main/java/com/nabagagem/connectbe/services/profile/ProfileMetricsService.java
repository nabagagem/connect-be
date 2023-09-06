package com.nabagagem.connectbe.services.profile;

import com.nabagagem.connectbe.domain.profile.ProfileMetrics;
import com.nabagagem.connectbe.repos.BidRepository;
import com.nabagagem.connectbe.repos.ProfileRepo;
import com.nabagagem.connectbe.repos.ProfileReportRepository;
import com.nabagagem.connectbe.repos.RatingRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ProfileMetricsService {
    private final BidRepository bidRepository;
    private final RatingRepository ratingRepository;
    private final ProfileReportRepository profileReportRepository;
    private final ProfileRepo profileRepo;

    public Optional<ProfileMetrics> getMetricsFor(UUID id) {
        return Optional.empty();
    }
}
