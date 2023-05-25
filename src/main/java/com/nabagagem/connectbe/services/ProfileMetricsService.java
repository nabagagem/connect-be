package com.nabagagem.connectbe.services;

import com.nabagagem.connectbe.domain.JobStatus;
import com.nabagagem.connectbe.domain.ProfileMetrics;
import com.nabagagem.connectbe.entities.Audit;
import com.nabagagem.connectbe.repos.BidRepository;
import com.nabagagem.connectbe.repos.ProfileRepo;
import com.nabagagem.connectbe.repos.ProfileReportRepository;
import com.nabagagem.connectbe.repos.RatingRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
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
        return profileRepo.findById(id)
                .map(profile -> {
                    LocalDateTime firstLogin = Optional.ofNullable(profile.getAudit())
                            .map(Audit::getCreatedAt)
                            .orElse(null);

                    Map<JobStatus, Long> summary = bidRepository.countByStatus(id);
                    Long amountOfHours = bidRepository.sumHoursPerUser(id);
                    Long recommendations = ratingRepository.countByTargetProfileId(id);
                    Long violations = profileReportRepository.countByTargetProfileId(id);

                    return new ProfileMetrics(
                            summary.get(JobStatus.FINISHED),
                            summary.get(JobStatus.ONGOING),
                            amountOfHours,
                            recommendations,
                            violations,
                            Optional.ofNullable(profile.getLastActivity())
                                    .orElseGet(() -> Optional.ofNullable(profile.getAudit())
                                            .map(Audit::getModifiedAt).orElse(null)),
                            firstLogin
                    );
                });
    }
}
