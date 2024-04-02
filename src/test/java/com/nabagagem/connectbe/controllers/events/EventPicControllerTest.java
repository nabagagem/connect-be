package com.nabagagem.connectbe.controllers.events;

import com.nabagagem.connectbe.controllers.LoginHelper;
import com.nabagagem.connectbe.controllers.MediaControllerHelper;
import com.nabagagem.connectbe.domain.notification.EventPicCommand;
import com.nabagagem.connectbe.entities.Media;
import com.nabagagem.connectbe.services.events.EventPicService;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;

@ExtendWith(SpringExtension.class)
@WebMvcTest(EventPicController.class)
@WithMockUser
class EventPicControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventPicService mockEventPicService;
    @MockBean
    private MediaControllerHelper mockMediaControllerHelper;

    @MockBean
    private LoginHelper loginHelper;

    @Test
    void testGet() throws Exception {
        // Setup
        when(mockEventPicService.getPicFor(UUID.fromString("0c3290c9-ff0d-4e4c-b9f4-86a534b47653")))
                .thenReturn(Optional.of(Media.builder().build()));

        // Configure MediaControllerHelper.toResponse(...).
        final ResponseEntity<byte[]> responseEntity = new ResponseEntity<>("content".getBytes(),
                HttpStatus.OK);
        when(mockMediaControllerHelper.toResponse(Media.builder().build())).thenReturn(responseEntity);

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(
                        get("/api/v1/events/{id}/pic", "0c3290c9-ff0d-4e4c-b9f4-86a534b47653")
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("content");
    }

    @Test
    void testGet_EventPicServiceReturnsAbsent() throws Exception {
        // Setup
        when(mockEventPicService.getPicFor(UUID.fromString("0c3290c9-ff0d-4e4c-b9f4-86a534b47653")))
                .thenReturn(Optional.empty());

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(
                        get("/api/v1/events/{id}/pic", "0c3290c9-ff0d-4e4c-b9f4-86a534b47653")
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void testUpload() throws Exception {
        // Setup
        // Run the test
        MockMultipartFile file = new MockMultipartFile("file", "originalFilename", MediaType.APPLICATION_JSON_VALUE,
                "content".getBytes());
        final MockHttpServletResponse response = mockMvc.perform(
                        multipart("/api/v1/events/{id}/pic", "56bea0b1-67ca-4a54-8ecb-db0488f05bed")
                                .file(file)
                                .accept(MediaType.APPLICATION_JSON)
                                .with(csrf())
                                .with(request -> {
                                    request.setMethod("PUT");
                                    return request;
                                }))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        verify(mockMediaControllerHelper).validateFilePic(any(MultipartFile.class));
        verify(mockEventPicService).save(new EventPicCommand(UUID.fromString("56bea0b1-67ca-4a54-8ecb-db0488f05bed"),
                file));
    }
}
