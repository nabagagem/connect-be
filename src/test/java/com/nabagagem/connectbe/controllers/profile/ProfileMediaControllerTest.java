package com.nabagagem.connectbe.controllers.profile;

import com.nabagagem.connectbe.domain.profile.ProfileMediaItem;
import com.nabagagem.connectbe.entities.ProfileMedia;
import com.nabagagem.connectbe.services.profile.ProfileAuthService;
import com.nabagagem.connectbe.services.profile.ProfileMediaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WithMockUser
@ExtendWith(SpringExtension.class)
@WebMvcTest(ProfileMediaController.class)
class ProfileMediaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProfileMediaService mockProfileMediaService;
    @MockBean
    private ProfileAuthService mockProfileAuthService;

    @Test
    void testUpload() throws Exception {
        // Setup
        // Configure ProfileMediaService.create(...).
        final ProfileMedia profileMedia = ProfileMedia.builder()
                .id(UUID.fromString("cfb901ba-e4f2-46c1-becb-d3ba4198a0cf"))
                .build();
        when(mockProfileMediaService.create(eq(UUID.fromString("fe711f7d-be64-4b2a-aca9-b2ac272909b5")),
                any(MultipartFile.class))).thenReturn(profileMedia);

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(
                        multipart("/api/v1/profile/{profileId}/media", "fe711f7d-be64-4b2a-aca9-b2ac272909b5")
                                .file(new MockMultipartFile("file", "originalFilename", MediaType.APPLICATION_JSON_VALUE,
                                        "content".getBytes()))
                                .accept(MediaType.APPLICATION_JSON).with(csrf()))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        verify(mockProfileAuthService).failIfNotLoggedIn(UUID.fromString("fe711f7d-be64-4b2a-aca9-b2ac272909b5"));
    }

    @Test
    void testList() throws Exception {
        // Setup
        // Configure ProfileMediaService.listForProfile(...).
        final Set<ProfileMediaItem> profileMediaItems = Set.of(
                new ProfileMediaItem(UUID.fromString("8e9d6685-fecb-43c4-b5d3-0f050c9044d5"),
                        new MediaType("type", "subtype", StandardCharsets.UTF_8), "originalName"));
        when(mockProfileMediaService.listForProfile(
                UUID.fromString("9fd46831-931a-449e-82ea-00d726355627"))).thenReturn(profileMediaItems);

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(
                        get("/api/v1/profile/{profileId}/media", "9fd46831-931a-449e-82ea-00d726355627")
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThatJson(response.getContentAsString()).isEqualTo("""
                [{"id":"8e9d6685-fecb-43c4-b5d3-0f050c9044d5","mediaType":{"type":"type","subtype":"subtype","parameters":{"charset":"UTF-8"},
                "qualityValue":1.0,"wildcardType":false,"wildcardSubtype":false,"subtypeSuffix":null,"charset":"UTF-8","concrete":true},
                "originalName":"originalName"}]
                """);
        verify(mockProfileAuthService).failIfNotLoggedIn(UUID.fromString("9fd46831-931a-449e-82ea-00d726355627"));
    }

    @Test
    void testList_ProfileMediaServiceReturnsNoItems() throws Exception {
        // Setup
        when(mockProfileMediaService.listForProfile(
                UUID.fromString("9fd46831-931a-449e-82ea-00d726355627"))).thenReturn(Collections.emptySet());

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(
                        get("/api/v1/profile/{profileId}/media", "9fd46831-931a-449e-82ea-00d726355627")
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("[]");
        verify(mockProfileAuthService).failIfNotLoggedIn(UUID.fromString("9fd46831-931a-449e-82ea-00d726355627"));
    }

    @Test
    void testDelete() throws Exception {
        // Setup
        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(
                        delete("/api/v1/profile/{profileId}/media/{mediaId}", "8a461c1a-7346-422a-b021-a27447cdb011",
                                "768b7e3a-64a7-49bd-b215-b192119d1b81")
                                .accept(MediaType.APPLICATION_JSON).with(csrf()))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        verify(mockProfileAuthService).failIfNotLoggedIn(UUID.fromString("8a461c1a-7346-422a-b021-a27447cdb011"));
        verify(mockProfileMediaService).delete(UUID.fromString("768b7e3a-64a7-49bd-b215-b192119d1b81"));
    }
}
