package com.nabagagem.connectbe.controllers.profile;

import com.nabagagem.connectbe.controllers.LoginHelper;
import com.nabagagem.connectbe.services.profile.DeleteUserService;
import com.nabagagem.connectbe.services.profile.ProfileAuthService;
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
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

@WithMockUser
@ExtendWith(SpringExtension.class)
@WebMvcTest(DeleteProfileController.class)
class DeleteProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DeleteUserService mockDeleteUserService;
    @MockBean
    private ProfileAuthService mockProfileAuthService;
    @MockBean
    private LoginHelper loginHelper;

    @Test
    void testDelete() throws Exception {
        // Setup
        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(
                        delete("/api/v1/profile/{id}", "5f129ac7-fc07-4be8-82b1-9bd2213cf952")
                                .accept(MediaType.APPLICATION_JSON).with(csrf()))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        verify(mockProfileAuthService).failIfNotCurrentProfile(UUID.fromString("5f129ac7-fc07-4be8-82b1-9bd2213cf952"));
        verify(mockDeleteUserService).delete(UUID.fromString("5f129ac7-fc07-4be8-82b1-9bd2213cf952"));
    }
}
