package com.nabagagem.connectbe.controllers.profile;

import com.nabagagem.connectbe.controllers.MediaControllerHelper;
import com.nabagagem.connectbe.domain.profile.ProfilePicCommand;
import com.nabagagem.connectbe.entities.Media;
import com.nabagagem.connectbe.services.profile.ProfileAuthService;
import com.nabagagem.connectbe.services.profile.ProfilePicService;
import com.nabagagem.connectbe.services.profile.SlugService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ProfilePicController.class)
@WithMockUser
class ProfilePicControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProfilePicService mockProfilePicService;
    @MockBean
    private SlugService mockSlugService;
    @MockBean
    private MediaControllerHelper mockMediaControllerHelper;
    @MockBean
    private ProfileAuthService mockProfileAuthService;

    @Test
    void testUpload() throws Exception {
        // Setup
        // Configure SlugService.getProfileIdFrom(...).
        final UUID uuid = UUID.fromString("4bcc9b87-a2ab-4def-b526-e5f4dda1230f");
        when(mockSlugService.getProfileIdFrom("id")).thenReturn(uuid);

        // Run the test
        MockMultipartFile file = new MockMultipartFile("file", "originalFilename", MediaType.APPLICATION_JSON_VALUE,
                "content".getBytes());
        final MockHttpServletResponse response = mockMvc.perform(multipart("/api/v1/profile/{id}/pic", "id")
                        .file(file)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .with(csrf()))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        verify(mockMediaControllerHelper).validateFile(file);
        verify(mockProfileAuthService).failIfNotLoggedIn(uuid);
        verify(mockProfilePicService).save(
                new ProfilePicCommand(uuid,
                        file));
    }

    @Test
    void testGet() throws Exception {
        // Setup
        // Configure SlugService.getProfileIdFrom(...).
        final UUID uuid = UUID.fromString("eefb4df5-2c49-4ce2-ba10-e49ea17d6ede");
        when(mockSlugService.getProfileIdFrom("id")).thenReturn(uuid);

        Media media = Media.builder()
                .fileUrl("fileUrl")
                .build();

        when(mockProfilePicService.getPicFor(UUID.fromString("eefb4df5-2c49-4ce2-ba10-e49ea17d6ede")))
                .thenReturn(Optional.of(media));

        // Configure MediaControllerHelper.toResponse(...).
        final ResponseEntity<byte[]> responseEntity = new ResponseEntity<>("content".getBytes(),
                HttpStatus.OK);
        when(mockMediaControllerHelper.toResponse(media)).thenReturn(responseEntity);

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(get("/api/v1/profile/{id}/pic", "id")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("content");
    }

    @Test
    void testGet_ProfilePicServiceReturnsAbsent() throws Exception {
        // Setup
        // Configure SlugService.getProfileIdFrom(...).
        final UUID uuid = UUID.fromString("eefb4df5-2c49-4ce2-ba10-e49ea17d6ede");
        when(mockSlugService.getProfileIdFrom("id")).thenReturn(uuid);

        when(mockProfilePicService.getPicFor(UUID.fromString("eefb4df5-2c49-4ce2-ba10-e49ea17d6ede")))
                .thenReturn(Optional.empty());

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(get("/api/v1/profile/{id}/pic", "id")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }
}
