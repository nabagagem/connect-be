package com.nabagagem.connectbe.controllers.profile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nabagagem.connectbe.controllers.LoginHelper;
import com.nabagagem.connectbe.entities.Gdpr;
import com.nabagagem.connectbe.entities.GdprLevel;
import com.nabagagem.connectbe.services.profile.GdprService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@ExtendWith(SpringExtension.class)
@WebMvcTest(GdprController.class)
@WithMockUser("3f6bd9b9-374f-43dc-9576-8fe5913fe07a")
class GdprControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GdprService mockGdprService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LoginHelper loginHelper;

    @Test
    void testUpdate() throws Exception {
        // Setup
        // Run the test
        Gdpr value = new Gdpr();
        value.setGdprLevels(Collections.singleton(GdprLevel.MARKETING));
        final MockHttpServletResponse response = mockMvc.perform(
                        put("/api/v1/profile/{profileId}/gdpr", "3f6bd9b9-374f-43dc-9576-8fe5913fe07a")
                                .content(objectMapper.writeValueAsString(value))
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        verify(mockGdprService).update(eq(UUID.fromString("3f6bd9b9-374f-43dc-9576-8fe5913fe07a")), any(Gdpr.class));
    }

    @Test
    void testGet() throws Exception {
        // Setup
        // Configure GdprService.get(...).
        when(mockGdprService.get(UUID.fromString("3f6bd9b9-374f-43dc-9576-8fe5913fe07a")))
                .thenReturn(Collections.singleton(GdprLevel.STRICT));

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(
                        get("/api/v1/profile/{profileId}/gdpr", "3f6bd9b9-374f-43dc-9576-8fe5913fe07a")
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThatJson(response.getContentAsString()).isEqualTo("{\"gdprLevels\":[\"STRICT\"]}");
    }

    @Test
    void testGet_GdprServiceReturnsAbsent() throws Exception {
        // Setup
        when(mockGdprService.get(UUID.fromString("3b518778-fe92-4b21-9fda-81cbb18b6604"))).thenReturn(Set.of());

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(
                        get("/api/v1/profile/{profileId}/gdpr", "3b518778-fe92-4b21-9fda-81cbb18b6604")
                                .with(user("username"))
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThatJson(response.getContentAsString()).isEqualTo("{\"gdprLevels\":[]}");
    }
}
