package com.nabagagem.connectbe.services.profile;

import com.nabagagem.connectbe.domain.profile.ProfileMetrics;
import com.nabagagem.connectbe.entities.Audit;
import com.nabagagem.connectbe.entities.ConnectProfile;
import com.nabagagem.connectbe.repos.JobRepo;
import com.nabagagem.connectbe.repos.ProfileRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProfileMetricsServiceTest {

    @Mock
    private ProfileRepo mockProfileRepo;
    @Mock
    private JobRepo mockJobRepo;

    private ProfileMetricsService profileMetricsServiceUnderTest;

    @BeforeEach
    void setUp() throws Exception {
        profileMetricsServiceUnderTest = new ProfileMetricsService(mockProfileRepo, mockJobRepo);
    }

    @Test
    void testGetMetricsFor1() {
        // Setup
        Audit audit = new Audit();
        audit.setCreatedAt(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneId.of("UTC")));
        final ConnectProfile profile = ConnectProfile.builder()
                .lastActivity(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneId.of("UTC")))
                .audit(audit)
                .build();
        final Optional<ProfileMetrics> expectedResult = Optional.of(
                new ProfileMetrics(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneId.of("UTC")),
                        ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneId.of("UTC"))));

        // Run the test
        final Optional<ProfileMetrics> result = profileMetricsServiceUnderTest.getMetricsFor(profile);

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testGetMetricsFor2() {
        // Setup
        Audit audit = new Audit();
        audit.setCreatedAt(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneId.of("UTC")));
        final Optional<ProfileMetrics> expectedResult = Optional.of(
                new ProfileMetrics(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneId.of("UTC")),
                        ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneId.of("UTC"))));

        // Configure ProfileRepo.findById(...).
        final Optional<ConnectProfile> connectProfile = Optional.of(ConnectProfile.builder()
                .lastActivity(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneId.of("UTC")))
                .audit(audit)
                .build());
        when(mockProfileRepo.findById(UUID.fromString("10ba5667-5291-4f49-91be-f96711cfdb09")))
                .thenReturn(connectProfile);

        // Run the test
        final Optional<ProfileMetrics> result = profileMetricsServiceUnderTest.getMetricsFor(
                UUID.fromString("10ba5667-5291-4f49-91be-f96711cfdb09"));

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testGetMetricsFor2_ProfileRepoReturnsAbsent() {
        // Setup
        when(mockProfileRepo.findById(UUID.fromString("10ba5667-5291-4f49-91be-f96711cfdb09")))
                .thenReturn(Optional.empty());

        // Run the test
        final Optional<ProfileMetrics> result = profileMetricsServiceUnderTest.getMetricsFor(
                UUID.fromString("10ba5667-5291-4f49-91be-f96711cfdb09"));

        // Verify the results
        assertThat(result).isEmpty();
    }
}
