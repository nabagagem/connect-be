package com.nabagagem.connectbe.controllers.messages;

import com.nabagagem.connectbe.controllers.MediaControllerHelper;
import com.nabagagem.connectbe.domain.messages.MessagePatchPayload;
import com.nabagagem.connectbe.domain.profile.CreateMessageFileCommand;
import com.nabagagem.connectbe.entities.Media;
import com.nabagagem.connectbe.entities.Message;
import com.nabagagem.connectbe.services.messages.MessageAuthService;
import com.nabagagem.connectbe.services.messages.MessageFileService;
import com.nabagagem.connectbe.services.messages.MessageService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(MessageController.class)
@WithMockUser("32804943-a9c2-463e-a3ce-218e32244c43")
class MessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MessageService mockMessageService;
    @MockBean
    private MessageFileService mockMessageFileService;
    @MockBean
    private MediaControllerHelper mockMediaControllerHelper;
    @MockBean
    private MessageAuthService mockMessageAuthService;

    @Test
    void testUpload() throws Exception {
        // Setup
        MockMultipartFile file = new MockMultipartFile("file", "originalFilename", MediaType.APPLICATION_JSON_VALUE,
                "content".getBytes());
        UUID threadId = UUID.fromString("9f5b98c7-7d5f-44f6-a4a2-5fe5e3ac2946");
        UUID id = UUID.fromString("9f5b98c7-7d5f-44f6-a4a2-5fe5e3ac2946");
        when(mockMessageFileService.create(
                new CreateMessageFileCommand(file, "text",
                        threadId))).thenReturn(Message.builder().id(id).build());

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(
                        multipart("/api/v1/threads/{threadId}/files", threadId)
                                .file(file)
                                .with(csrf())
                                .param("text", "text")
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("{\"id\":\"9f5b98c7-7d5f-44f6-a4a2-5fe5e3ac2946\"}");
        verify(mockMessageAuthService).failIfUnableToWriteOnThread(
                threadId);
    }

    @Test
    void testDelete() throws Exception {
        // Setup
        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(
                        delete("/api/v1/messages/{id}", "93574c21-76bd-49f0-b131-86fb3dbff9fa")
                                .with(csrf())
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        verify(mockMessageAuthService).failIfUnableToDelete(UUID.fromString("93574c21-76bd-49f0-b131-86fb3dbff9fa"));
        verify(mockMessageService).delete(UUID.fromString("93574c21-76bd-49f0-b131-86fb3dbff9fa"));
    }

    @Test
    void testUpdate() throws Exception {
        // Setup
        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(
                        patch("/api/v1/messages/{id}", "875f0e17-b8df-4d02-8ca7-5188b212a32a")
                                .with(csrf())
                                .content("""
                                        {
                                          "read": true,
                                          "text": "foobar"
                                        }
                                        """).contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        MessagePatchPayload patchPayload = new MessagePatchPayload("foobar", true);
        verify(mockMessageAuthService).failIfUnableToPatch(UUID.fromString("875f0e17-b8df-4d02-8ca7-5188b212a32a"),
                patchPayload);
        verify(mockMessageService).update(UUID.fromString("875f0e17-b8df-4d02-8ca7-5188b212a32a"),
                patchPayload);
    }

    @Test
    void testGet() throws Exception {
        // Setup
        Media media = Media.builder().build();
        when(mockMessageFileService.getPicFor(UUID.fromString("68931f69-9eeb-4cdf-8741-d69edb8b7180")))
                .thenReturn(Optional.of(media));

        // Configure MediaControllerHelper.toResponse(...).
        final ResponseEntity<byte[]> responseEntity = new ResponseEntity<>("content".getBytes(),
                HttpStatus.OK);
        when(mockMediaControllerHelper.toResponse(media)).thenReturn(responseEntity);

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(
                        get("/api/v1/messages/{id}/file", "68931f69-9eeb-4cdf-8741-d69edb8b7180")
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("content");
        verify(mockMessageAuthService).failIfUnableToRead(UUID.fromString("68931f69-9eeb-4cdf-8741-d69edb8b7180"));
    }

    @Test
    void testGet_MessageFileServiceReturnsAbsent() throws Exception {
        // Setup
        when(mockMessageFileService.getPicFor(UUID.fromString("68931f69-9eeb-4cdf-8741-d69edb8b7180")))
                .thenReturn(Optional.empty());

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(
                        get("/api/v1/messages/{id}/file", "68931f69-9eeb-4cdf-8741-d69edb8b7180")
                                .with(csrf())
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        verify(mockMessageAuthService).failIfUnableToRead(UUID.fromString("68931f69-9eeb-4cdf-8741-d69edb8b7180"));
    }
}
