package com.nabagagem.connectbe.controllers.job;

import com.nabagagem.connectbe.controllers.MediaControllerHelper;
import com.nabagagem.connectbe.domain.FilePurpose;
import com.nabagagem.connectbe.domain.job.DeleteJobFileCommand;
import com.nabagagem.connectbe.domain.job.GetJobMediaCommand;
import com.nabagagem.connectbe.domain.job.JobFileCommand;
import com.nabagagem.connectbe.entities.Media;
import com.nabagagem.connectbe.services.jobs.JobAuthService;
import com.nabagagem.connectbe.services.jobs.JobFileService;
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

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(JobFileController.class)
@WithMockUser
class JobFileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JobFileService mockJobFileService;
    @MockBean
    private MediaControllerHelper mockMediaControllerHelper;
    @MockBean
    private JobAuthService mockJobAuthService;

    @Test
    void testUploadOnPosition() throws Exception {
        // Setup
        // Run the test
        MockMultipartFile file = new MockMultipartFile("file", "originalFilename", MediaType.APPLICATION_JSON_VALUE,
                "content".getBytes());
        final MockHttpServletResponse response = mockMvc.perform(
                        multipart("/api/v1/jobs/{jobId}/files/{filePurpose}/{position}", "ea47a2e3-79f8-45e4-9064-31a3688fc818",
                                FilePurpose.PIC, 0)
                                .file(file)
                                .with(csrf())
                                .with(request -> {
                                    request.setMethod("PUT");
                                    return request;
                                })
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        verify(mockJobAuthService).failIfUnauthorized(UUID.fromString("ea47a2e3-79f8-45e4-9064-31a3688fc818"));
        verify(mockJobFileService).create(
                new JobFileCommand(UUID.fromString("ea47a2e3-79f8-45e4-9064-31a3688fc818"), FilePurpose.PIC, 0,
                        file));
    }

    @Test
    void testList_JobFileServiceReturnsNoItems() throws Exception {
        // Setup
        when(mockJobFileService.listFor(UUID.fromString("43b4fc99-aa6c-43ff-a549-dbfe670ed043")))
                .thenReturn(Collections.emptySet());

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(
                        get("/api/v1/jobs/{jobId}/files", "43b4fc99-aa6c-43ff-a549-dbfe670ed043")
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("[]");
    }

    @Test
    void testGetJobMediaOnPosition() throws Exception {
        // Setup
        UUID jobId = UUID.fromString("be4d73dc-eb25-43fc-8dc9-a725d9c59ceb");
        when(mockJobFileService.getMediaFrom(
                new GetJobMediaCommand(jobId, FilePurpose.PIC,
                        0))).thenReturn(Optional.of(Media.builder().build()));

        // Configure MediaControllerHelper.toResponse(...).
        final ResponseEntity<byte[]> responseEntity = new ResponseEntity<>("content".getBytes(),
                HttpStatus.OK);
        when(mockMediaControllerHelper.toResponse(Media.builder().build())).thenReturn(responseEntity);

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(
                        get("/api/v1/jobs/{jobId}/files/{filePurpose}/{position}", jobId,
                                FilePurpose.PIC, 0)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("content");
    }

    @Test
    void testGetJobMediaOnPosition_JobFileServiceReturnsAbsent() throws Exception {
        // Setup
        when(mockJobFileService.getMediaFrom(
                new GetJobMediaCommand(UUID.fromString("be4d73dc-eb25-43fc-8dc9-a725d9c59ceb"), FilePurpose.PIC,
                        0))).thenReturn(Optional.empty());

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(
                        get("/api/v1/jobs/{jobId}/files/{filePurpose}/{position}", "f11579b9-85c3-4059-baf6-4c6a8d63154c",
                                FilePurpose.PIC, 0)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void testDeleteOnPosition() throws Exception {
        // Setup
        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(
                        delete("/api/v1/jobs/{jobId}/files/{filePurpose}/{position}", "be8b95f1-7669-4e99-a8a4-28005c6bb3b3",
                                FilePurpose.PIC, 0)
                                .accept(MediaType.APPLICATION_JSON).with(csrf()))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        verify(mockJobAuthService).failIfUnauthorized(UUID.fromString("be8b95f1-7669-4e99-a8a4-28005c6bb3b3"));
        verify(mockJobFileService).delete(
                new DeleteJobFileCommand(UUID.fromString("be8b95f1-7669-4e99-a8a4-28005c6bb3b3"), FilePurpose.PIC, 0));
    }

    @Test
    void testDelete() throws Exception {
        // Setup
        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(
                        delete("/api/v1/jobs/{jobId}/files/{filePurpose}", "be8b95f1-7669-4e99-a8a4-28005c6bb3b3",
                                FilePurpose.PIC)
                                .accept(MediaType.APPLICATION_JSON).with(csrf()))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        verify(mockJobAuthService).failIfUnauthorized(UUID.fromString("be8b95f1-7669-4e99-a8a4-28005c6bb3b3"));
        verify(mockJobFileService).delete(
                new DeleteJobFileCommand(UUID.fromString("be8b95f1-7669-4e99-a8a4-28005c6bb3b3"), FilePurpose.PIC, 0));
    }

    @Test
    void testGetJobMedia() throws Exception {
        // Setup
        when(mockJobFileService.getMediaFrom(
                new GetJobMediaCommand(UUID.fromString("8a7358e3-8ba4-4311-b20e-c068f619fd93"), FilePurpose.PIC,
                        0))).thenReturn(Optional.of(Media.builder().build()));

        // Configure MediaControllerHelper.toResponse(...).
        final ResponseEntity<byte[]> responseEntity = new ResponseEntity<>("content".getBytes(),
                HttpStatus.OK);
        when(mockMediaControllerHelper.toResponse(Media.builder().build())).thenReturn(responseEntity);

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(
                        get("/api/v1/jobs/{jobId}/files/{filePurpose}", "8a7358e3-8ba4-4311-b20e-c068f619fd93", FilePurpose.PIC)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("content");
    }

    @Test
    void testGetJobMedia_JobFileServiceReturnsAbsent() throws Exception {
        // Setup
        when(mockJobFileService.getMediaFrom(
                new GetJobMediaCommand(UUID.fromString("8a7358e3-8ba4-4311-b20e-c068f619fd93"), FilePurpose.PIC,
                        0))).thenReturn(Optional.empty());

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(
                        get("/api/v1/jobs/{jobId}/files/{filePurpose}", "f11579b9-85c3-4059-baf6-4c6a8d63154c", FilePurpose.PIC)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }
}
