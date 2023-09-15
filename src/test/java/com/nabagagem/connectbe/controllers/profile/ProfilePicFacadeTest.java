package com.nabagagem.connectbe.controllers.profile;

import com.nabagagem.connectbe.controllers.MediaControllerHelper;
import com.nabagagem.connectbe.domain.profile.ProfilePicCommand;
import com.nabagagem.connectbe.entities.Media;
import com.nabagagem.connectbe.services.profile.ProfileAuthService;
import com.nabagagem.connectbe.services.profile.ProfilePicService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProfilePicFacadeTest {

    @Mock
    private ProfilePicService mockProfilePicService;
    @Mock
    private ProfileAuthService mockProfileAuthService;
    @Mock
    private MediaControllerHelper mockMediaControllerHelper;

    private ProfilePicFacade profilePicFacadeUnderTest;

    @BeforeEach
    void setUp() {
        profilePicFacadeUnderTest = new ProfilePicFacade(mockProfilePicService, mockProfileAuthService,
                mockMediaControllerHelper);
    }

    @Test
    void testSave() {
        // Setup
        UUID id = UUID.fromString("8a899aad-af5d-4a6d-8449-78176977abea");
        MockMultipartFile file = new MockMultipartFile("name", "content".getBytes());
        final ProfilePicCommand profilePicCommand = new ProfilePicCommand(
                id,
                file);

        // Run the test
        profilePicFacadeUnderTest.save(profilePicCommand);

        // Verify the results
        verify(mockMediaControllerHelper).validateFilePic(any(MultipartFile.class));
        verify(mockProfileAuthService).failIfNotLoggedIn(id);
        verify(mockProfilePicService).save(
                new ProfilePicCommand(id,
                        file));
    }

    @Test
    void testGetPicFor() {
        // Setup
        when(mockProfilePicService.getPicFor(UUID.fromString("81a0ff3a-24e2-4d55-970c-6beda57ee5a8")))
                .thenReturn(Optional.of(Media.builder().build()));

        // Configure MediaControllerHelper.toResponse(...).
        final ResponseEntity<byte[]> responseEntity = new ResponseEntity<>("content".getBytes(),
                HttpStatus.OK);
        when(mockMediaControllerHelper.toResponse(Media.builder().build())).thenReturn(responseEntity);

        // Run the test
        final ResponseEntity<byte[]> result = profilePicFacadeUnderTest.getPicFor(
                UUID.fromString("81a0ff3a-24e2-4d55-970c-6beda57ee5a8"));

        // Verify the results
        assertThat(new String(result.getBody()))
                .isEqualTo("content");
    }

    @Test
    void testGetPicFor_ProfilePicServiceReturnsAbsent() {
        // Setup
        when(mockProfilePicService.getPicFor(UUID.fromString("81a0ff3a-24e2-4d55-970c-6beda57ee5a8")))
                .thenReturn(Optional.empty());

        // Run the test
        final ResponseEntity<byte[]> result = profilePicFacadeUnderTest.getPicFor(
                UUID.fromString("81a0ff3a-24e2-4d55-970c-6beda57ee5a8"));

        // Verify the results
    }
}
