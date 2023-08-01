package com.nabagagem.connectbe.services.profile;

import com.nabagagem.connectbe.domain.profile.ProfilePicCommand;
import com.nabagagem.connectbe.entities.ConnectProfile;
import com.nabagagem.connectbe.entities.Media;
import com.nabagagem.connectbe.repos.ProfileRepo;
import com.nabagagem.connectbe.services.MediaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
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
    void testSave() {
        // Setup
        UUID profileId = UUID.fromString("2c277341-a5ad-43c1-97f6-5a977cb60079");
        final ProfilePicCommand profilePicCommand = new ProfilePicCommand(
                profileId,
                new MockMultipartFile("name", "content".getBytes()));

        // Configure ProfileService.findOrInit(...).
        Media media = Media.builder()
                .mediaType(MediaType.APPLICATION_JSON)
                .fileUrl("url")
                .build();
        final ConnectProfile profile = ConnectProfile.builder()
                .profilePicture(media)
                .build();
        when(mockProfileService.findOrInit(profileId))
                .thenReturn(profile);

        when(mockMediaService.upload(any(MultipartFile.class), eq(ConnectProfile.builder()
                .profilePicture(media)
                .build()))).thenReturn(media);

        // Run the test
        profilePicServiceUnderTest.save(profilePicCommand);

        // Verify the results
        verify(mockMediaService).delete(media);
        verify(mockProfileService).save(ConnectProfile.builder()
                .profilePicture(media)
                .build());
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
