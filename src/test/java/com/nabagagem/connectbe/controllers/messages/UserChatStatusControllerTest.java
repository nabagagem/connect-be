package com.nabagagem.connectbe.controllers.messages;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nabagagem.connectbe.domain.messages.ChatStatus;
import com.nabagagem.connectbe.domain.messages.UserChatStatusCommand;
import com.nabagagem.connectbe.services.messages.UserChatStatusService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@WithMockUser
@ExtendWith(SpringExtension.class)
@WebMvcTest(UserChatStatusController.class)
class UserChatStatusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserChatStatusService mockUserChatStatusService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testUpdate() throws Exception {
        // Setup
        // Run the test
        UserChatStatusCommand userChatStatusCommand = new UserChatStatusCommand(UUID.fromString("e15c6e65-4105-49fd-8303-a1a56d378cf2"), ChatStatus.TYPING);
        final MockHttpServletResponse response = mockMvc.perform(
                        post("/api/v1/threads/{threadId}/user-status", "680be17a-b583-4607-b428-fbecb0f47d3c")
                                .content(objectMapper.writeValueAsString(userChatStatusCommand))
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        verify(mockUserChatStatusService).update(UUID.fromString("680be17a-b583-4607-b428-fbecb0f47d3c"),
                userChatStatusCommand);
    }
}
