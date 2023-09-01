package com.nabagagem.connectbe.controllers.messages;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nabagagem.connectbe.domain.messages.ReactionPayload;
import com.nabagagem.connectbe.entities.Reaction;
import com.nabagagem.connectbe.services.messages.MessageAuthService;
import com.nabagagem.connectbe.services.messages.MessageReactionService;
import com.nabagagem.connectbe.services.messages.ReactAuthService;
import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.EmojiManager;
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
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ReactionController.class)
@WithMockUser
class ReactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MessageAuthService mockMessageAuthService;
    @MockBean
    private MessageReactionService mockMessageReactionService;
    @MockBean
    private ReactAuthService mockReactAuthService;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreate() throws Exception {
        // Setup
        // Configure MessageReactionService.create(...).
        final Reaction reaction = Reaction.builder()
                .id(UUID.fromString("f73e9f99-8d8a-4027-a8b2-ff89c95b1d06"))
                .build();
        ReactionPayload payload = new ReactionPayload(EmojiManager.getAll().stream()
                .map(Emoji::getUnicode)
                .findAny().orElse(""));
        when(mockMessageReactionService.create(UUID.fromString("42d85431-d4f3-46f8-ad23-2723bf46f8f5"),
                payload)).thenReturn(reaction);

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(
                        post("/api/v1/messages/{messageId}/reactions", "42d85431-d4f3-46f8-ad23-2723bf46f8f5")
                                .with(csrf())
                                .content(objectMapper.writeValueAsString(payload))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("{\"id\":\"f73e9f99-8d8a-4027-a8b2-ff89c95b1d06\"}");
        verify(mockMessageAuthService).failIfUnableToReact(UUID.fromString("42d85431-d4f3-46f8-ad23-2723bf46f8f5"));
    }

    @Test
    void testCreate_InvalidEmoji() throws Exception {
        // Setup
        ReactionPayload payload = new ReactionPayload("whatever");

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(
                        post("/api/v1/messages/{messageId}/reactions", "42d85431-d4f3-46f8-ad23-2723bf46f8f5")
                                .with(csrf())
                                .content(objectMapper.writeValueAsString(payload))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void testDelete() throws Exception {
        // Setup
        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(
                        delete("/api/v1/reactions/{reactionId}", "a5c6a5d9-59e6-43c1-b9ae-e3699bcfbc25")
                                .with(csrf())
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        verify(mockReactAuthService).failIfUnableToDelete(UUID.fromString("a5c6a5d9-59e6-43c1-b9ae-e3699bcfbc25"));
        verify(mockMessageReactionService).delete(UUID.fromString("a5c6a5d9-59e6-43c1-b9ae-e3699bcfbc25"));
    }
}
