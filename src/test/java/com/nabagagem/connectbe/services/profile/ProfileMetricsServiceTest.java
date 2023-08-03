package com.nabagagem.connectbe.services.profile;

import com.nabagagem.connectbe.domain.job.JobStatus;
import com.nabagagem.connectbe.domain.profile.ProfileMetrics;
import com.nabagagem.connectbe.entities.Audit;
import com.nabagagem.connectbe.entities.ConnectProfile;
import com.nabagagem.connectbe.repos.BidRepository;
import com.nabagagem.connectbe.repos.ProfileRepo;
import com.nabagagem.connectbe.repos.ProfileReportRepository;
import com.nabagagem.connectbe.repos.RatingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProfileMetricsServiceTest {

    @Mock
    private BidRepository mockBidRepository;
    @Mock
    private RatingRepository mockRatingRepository;
    @Mock
    private ProfileReportRepository mockProfileReportRepository;
    @Mock
    private ProfileRepo mockProfileRepo;

    private ProfileMetricsService profileMetricsServiceUnderTest;

    @BeforeEach
    void setUp() throws Exception {
        profileMetricsServiceUnderTest = new ProfileMetricsService(mockBidRepository, mockRatingRepository,
                mockProfileReportRepository, mockProfileRepo);
    }

    @Test
    void testGetMetricsFor() {
        // Setup
        final Optional<ProfileMetrics> expectedResult = Optional.of(new ProfileMetrics(null, null, 0L, 0L, 0L,
                ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC),
                null));

        // Configure ProfileRepo.findById(...).
        final Optional<ConnectProfile> connectProfile = Optional.of(ConnectProfile.builder()
                .lastActivity(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC))
                .audit(new Audit())
                .build());
        when(mockProfileRepo.findById(UUID.fromString("9c206dd9-4bf8-4a59-b266-90dfd8f8c074")))
                .thenReturn(connectProfile);

        when(mockBidRepository.countByStatus(UUID.fromString("9c206dd9-4bf8-4a59-b266-90dfd8f8c074")))
                .thenReturn(Map.ofEntries(Map.entry(JobStatus.PUBLISHED, 0L)));
        when(mockBidRepository.sumHoursPerUser(UUID.fromString("9c206dd9-4bf8-4a59-b266-90dfd8f8c074"))).thenReturn(0L);
        when(mockRatingRepository.countByTargetProfileId(
                UUID.fromString("9c206dd9-4bf8-4a59-b266-90dfd8f8c074"))).thenReturn(0L);
        when(mockProfileReportRepository.countByTargetProfileId(
                UUID.fromString("9c206dd9-4bf8-4a59-b266-90dfd8f8c074"))).thenReturn(0L);

        // Run the test
        final Optional<ProfileMetrics> result = profileMetricsServiceUnderTest.getMetricsFor(
                UUID.fromString("9c206dd9-4bf8-4a59-b266-90dfd8f8c074"));

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testGetMetricsFor_ProfileRepoReturnsAbsent() {
        // Setup
        when(mockProfileRepo.findById(UUID.fromString("9c206dd9-4bf8-4a59-b266-90dfd8f8c074")))
                .thenReturn(Optional.empty());

        // Run the test
        final Optional<ProfileMetrics> result = profileMetricsServiceUnderTest.getMetricsFor(
                UUID.fromString("9c206dd9-4bf8-4a59-b266-90dfd8f8c074"));

        // Verify the results
        assertThat(result).isEmpty();
    }
}
