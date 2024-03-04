package com.nabagagem.connectbe.controllers.profile;

import com.nabagagem.connectbe.services.profile.SlugService;
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

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WithMockUser
@ExtendWith(SpringExtension.class)
@WebMvcTest(UserOnlineController.class)
class UserOnlineControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OnlineUserService mockOnlineUserService;
    @MockBean
    private SlugService mockSlugService;

    @Test
    void testGet() throws Exception {
        // Setup
        // Configure SlugService.getProfileIdFrom(...).
        final UUID uuid = UUID.fromString("9b5a6929-1b9a-440e-ac66-67476feace36");
        when(mockSlugService.getProfileIdFrom("id")).thenReturn(uuid);

        when(mockOnlineUserService.isOnline(UUID.fromString("9b5a6929-1b9a-440e-ac66-67476feace36"))).thenReturn(false);

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(get("/api/v1/profile/{id}/online", "id")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("{\"online\":false}");
    }
}
