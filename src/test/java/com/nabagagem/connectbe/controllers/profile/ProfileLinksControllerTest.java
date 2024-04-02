package com.nabagagem.connectbe.controllers.profile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nabagagem.connectbe.controllers.LoginHelper;
import com.nabagagem.connectbe.domain.profile.ProfileLinksCommand;
import com.nabagagem.connectbe.entities.LinkType;
import com.nabagagem.connectbe.services.profile.ProfileLinkService;
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

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@WithMockUser
@ExtendWith(SpringExtension.class)
@WebMvcTest(ProfileLinksController.class)
class ProfileLinksControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProfileLinkService mockProfileLinkService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LoginHelper loginHelper;

    @Test
    void testPut() throws Exception {
        // Setup
        // Run the test
        UUID profileId = UUID.fromString("eea954a9-47bc-4a29-8b6a-a971d019ec69");
        Map<LinkType, String> body = Map.ofEntries(Map.entry(LinkType.FACEBOOK, "value"));
        final MockHttpServletResponse response = mockMvc.perform(
                        put("/api/v1/profile/{id}/links", profileId)
                                .content(objectMapper.writeValueAsString(body))
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        verify(mockProfileLinkService).update(
                new ProfileLinksCommand(profileId,
                        body));
    }
}
