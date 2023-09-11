package com.nabagagem.connectbe.services.profile;

import com.nabagagem.connectbe.entities.ConnectProfile;
import com.nabagagem.connectbe.entities.Media;
import com.nabagagem.connectbe.repos.ProfileRepo;
import com.nabagagem.connectbe.services.MediaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProfilePicServiceTest {

    @Mock
    private ProfileRepo mockProfileRepo;
    @Mock
    private ProfileService mockProfileService;
    @Mock
    private MediaService mockMediaService;

    private ProfilePicService profilePicServiceUnderTest;

    @BeforeEach
    void setUp() {
        profilePicServiceUnderTest = new ProfilePicService(mockProfileRepo, mockProfileService, mockMediaService);
    }


    @Test
    void testGetPicFor() {
        // Setup
        final Optional<Media> expectedResult = Optional.of(Media.builder().build());

        // Configure ProfileRepo.findById(...).
        final Optional<ConnectProfile> connectProfile = Optional.of(ConnectProfile.builder()
                .profilePicture(Media.builder().build())
                .build());
        when(mockProfileRepo.findById(UUID.fromString("19e0314c-ed27-4c2a-8342-63a8e86da00f")))
                .thenReturn(connectProfile);

        // Run the test
        final Optional<Media> result = profilePicServiceUnderTest.getPicFor(
                UUID.fromString("19e0314c-ed27-4c2a-8342-63a8e86da00f"));

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testGetPicFor_ProfileRepoReturnsAbsent() {
        // Setup
        when(mockProfileRepo.findById(UUID.fromString("19e0314c-ed27-4c2a-8342-63a8e86da00f")))
                .thenReturn(Optional.empty());

        // Run the test
        final Optional<Media> result = profilePicServiceUnderTest.getPicFor(
                UUID.fromString("19e0314c-ed27-4c2a-8342-63a8e86da00f"));

        // Verify the results
        assertThat(result).isEmpty();
    }
}
