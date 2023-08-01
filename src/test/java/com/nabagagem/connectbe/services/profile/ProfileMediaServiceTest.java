package com.nabagagem.connectbe.services.profile;

import com.nabagagem.connectbe.domain.profile.ProfileMediaItem;
import com.nabagagem.connectbe.entities.ConnectProfile;
import com.nabagagem.connectbe.entities.Media;
import com.nabagagem.connectbe.entities.ProfileMedia;
import com.nabagagem.connectbe.repos.ProfileMediaRepository;
import com.nabagagem.connectbe.services.MediaService;
import com.nabagagem.connectbe.services.mappers.ProfileMediaMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfileMediaServiceTest {

    @Mock
    private MediaService mockMediaService;
    @Mock
    private ProfileMediaRepository mockProfileMediaRepository;
    @Mock
    private ProfileMediaMapper mockProfileMediaMapper;

    private ProfileMediaService profileMediaServiceUnderTest;
    @Captor
    private ArgumentCaptor<ProfileMedia> mediaArgumentCaptor;

    @BeforeEach
    void setUp() {
        profileMediaServiceUnderTest = new ProfileMediaService(mockMediaService, mockProfileMediaRepository,
                mockProfileMediaMapper);
    }

    @Test
    void testCreate() {
        // Setup
        final MultipartFile file = new MockMultipartFile("name", "content".getBytes());
        Media media = Media.builder()
                .mediaType(MediaType.APPLICATION_JSON)
                .build();
        when(mockMediaService.upload(any(MultipartFile.class))).thenReturn(media);

        // Configure ProfileMediaRepository.save(...).
        final ProfileMedia profileMedia = ProfileMedia.builder()
                .profile(ConnectProfile.builder()
                        .id(UUID.fromString("23900d8f-7a8d-49bb-82cc-ec17a9aff540"))
                        .build())
                .media(media)
                .build();
        when(mockProfileMediaRepository.save(any(ProfileMedia.class))).thenReturn(profileMedia);

        // Run the test
        final ProfileMedia result = profileMediaServiceUnderTest.create(
                UUID.fromString("23900d8f-7a8d-49bb-82cc-ec17a9aff540"), file);

        // Verify the results
        assertThat(profileMedia).isEqualTo(result);
        verify(mockProfileMediaRepository).save(mediaArgumentCaptor.capture());
        assertThat(mediaArgumentCaptor.getValue())
                .usingRecursiveComparison()
                .ignoringFields("id", "audit")
                .isEqualTo(profileMedia);
    }

    @Test
    void testCreate_ProfileMediaRepositoryThrowsOptimisticLockingFailureException() {
        // Setup
        final MultipartFile file = new MockMultipartFile("name", "content".getBytes());
        when(mockMediaService.upload(any(MultipartFile.class))).thenReturn(Media.builder().build());
        when(mockProfileMediaRepository.save(any(ProfileMedia.class)))
                .thenThrow(OptimisticLockingFailureException.class);

        // Run the test
        assertThatThrownBy(
                () -> profileMediaServiceUnderTest.create(UUID.fromString("23900d8f-7a8d-49bb-82cc-ec17a9aff540"),
                        file)).isInstanceOf(OptimisticLockingFailureException.class);
    }

    @Test
    void testListForProfile() {
        // Setup
        final Set<ProfileMediaItem> expectedResult = Set.of(
                new ProfileMediaItem(UUID.fromString("4045aaf4-862e-4ec4-8462-a230ac889e05"),
                        new MediaType("type", "subtype", StandardCharsets.UTF_8), "originalName"));

        // Configure ProfileMediaRepository.getProfileMedia(...).
        final Set<ProfileMedia> profileMedia = Set.of(ProfileMedia.builder()
                .profile(ConnectProfile.builder()
                        .id(UUID.fromString("23900d8f-7a8d-49bb-82cc-ec17a9aff540"))
                        .build())
                .media(Media.builder().build())
                .build());
        when(mockProfileMediaRepository.getProfileMedia(
                UUID.fromString("8cb16b3f-30f7-407c-b03b-c2a7dff9c636"))).thenReturn(profileMedia);

        // Configure ProfileMediaMapper.toDto(...).
        final ProfileMediaItem profileMediaItem = new ProfileMediaItem(
                UUID.fromString("a4107795-4010-467b-ae54-544f1fddfe05"),
                new MediaType("type", "subtype", StandardCharsets.UTF_8), "originalName");
        when(mockProfileMediaMapper.toDto(any(ProfileMedia.class))).thenReturn(profileMediaItem);

        // Run the test
        final Set<ProfileMediaItem> result = profileMediaServiceUnderTest.listForProfile(
                UUID.fromString("8cb16b3f-30f7-407c-b03b-c2a7dff9c636"));

        // Verify the results
        assertThat(result)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expectedResult);
    }

    @Test
    void testListForProfile_ProfileMediaRepositoryReturnsNoItems() {
        // Setup
        when(mockProfileMediaRepository.getProfileMedia(
                UUID.fromString("8cb16b3f-30f7-407c-b03b-c2a7dff9c636"))).thenReturn(Collections.emptySet());

        // Run the test
        final Set<ProfileMediaItem> result = profileMediaServiceUnderTest.listForProfile(
                UUID.fromString("8cb16b3f-30f7-407c-b03b-c2a7dff9c636"));

        // Verify the results
        assertThat(result).isEqualTo(Collections.emptySet());
    }

    @Test
    void testDelete() {
        // Setup
        // Configure ProfileMediaRepository.findById(...).
        final Optional<ProfileMedia> profileMedia = Optional.of(ProfileMedia.builder()
                .profile(ConnectProfile.builder()
                        .id(UUID.fromString("23900d8f-7a8d-49bb-82cc-ec17a9aff540"))
                        .build())
                .media(Media.builder().build())
                .build());
        when(mockProfileMediaRepository.findById(UUID.fromString("8d162278-154b-4693-bbb1-2bcf0f59698d")))
                .thenReturn(profileMedia);

        // Run the test
        profileMediaServiceUnderTest.delete(UUID.fromString("8d162278-154b-4693-bbb1-2bcf0f59698d"));

        // Verify the results
        verify(mockMediaService).delete(Media.builder().build());
        verify(mockProfileMediaRepository).delete(any(ProfileMedia.class));
    }

    @Test
    void testDelete_ProfileMediaRepositoryFindByIdReturnsAbsent() {
        // Setup
        when(mockProfileMediaRepository.findById(UUID.fromString("8d162278-154b-4693-bbb1-2bcf0f59698d")))
                .thenReturn(Optional.empty());

        // Run the test
        profileMediaServiceUnderTest.delete(UUID.fromString("8d162278-154b-4693-bbb1-2bcf0f59698d"));

        // Verify the results
    }

    @Test
    void testDelete_ProfileMediaRepositoryDeleteThrowsOptimisticLockingFailureException() {
        // Setup
        // Configure ProfileMediaRepository.findById(...).
        final Optional<ProfileMedia> profileMedia = Optional.of(ProfileMedia.builder()
                .profile(ConnectProfile.builder()
                        .id(UUID.fromString("23900d8f-7a8d-49bb-82cc-ec17a9aff540"))
                        .build())
                .media(Media.builder().build())
                .build());
        when(mockProfileMediaRepository.findById(UUID.fromString("8d162278-154b-4693-bbb1-2bcf0f59698d")))
                .thenReturn(profileMedia);

        doThrow(OptimisticLockingFailureException.class).when(mockProfileMediaRepository).delete(
                any(ProfileMedia.class));

        // Run the test
        assertThatThrownBy(() -> profileMediaServiceUnderTest.delete(
                UUID.fromString("8d162278-154b-4693-bbb1-2bcf0f59698d")))
                .isInstanceOf(OptimisticLockingFailureException.class);
        verify(mockMediaService).delete(Media.builder().build());
    }
}
