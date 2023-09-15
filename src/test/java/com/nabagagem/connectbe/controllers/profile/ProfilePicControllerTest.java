package com.nabagagem.connectbe.controllers.profile;

import com.nabagagem.connectbe.domain.profile.ProfilePicCommand;
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
    private SlugService mockSlugService;
    @MockBean
    private ProfilePicFacade mockProfilePicFacade;

    @Test
    void testUpload() throws Exception {
        // Setup
        // Configure SlugService.getProfileIdFrom(...).
        final UUID uuid = UUID.fromString("07bb384a-a49f-45f7-8fa4-5ac496919460");
        when(mockSlugService.getProfileIdFrom("id")).thenReturn(uuid);

        // Run the test
        MockMultipartFile file = new MockMultipartFile("file", "name", MediaType.APPLICATION_JSON_VALUE,
                "content".getBytes());
        final MockHttpServletResponse response = mockMvc.perform(multipart("/api/v1/profile/{id}/pic", "id")
                        .file(file).with(csrf()).with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        verify(mockProfilePicFacade).save(new ProfilePicCommand(uuid,
                file));
    }

    @Test
    void testGet() throws Exception {
        // Setup
        // Configure SlugService.getProfileIdFrom(...).
        final UUID uuid = UUID.fromString("05a3b422-d1d6-481f-a41e-b1a88c123880");
        when(mockSlugService.getProfileIdFrom("id")).thenReturn(uuid);

        // Configure ProfilePicFacade.getPicFor(...).
        final ResponseEntity<byte[]> responseEntity = new ResponseEntity<>("content".getBytes(),
                HttpStatus.OK);
        when(mockProfilePicFacade.getPicFor(UUID.fromString("05a3b422-d1d6-481f-a41e-b1a88c123880")))
                .thenReturn(responseEntity);

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(get("/api/v1/profile/{id}/pic", "id")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("content");
    }
}
