package com.nabagagem.connectbe.controllers.job;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nabagagem.connectbe.domain.job.JobCategory;
import com.nabagagem.connectbe.domain.job.JobFrequency;
import com.nabagagem.connectbe.domain.job.JobMode;
import com.nabagagem.connectbe.domain.job.JobPatchPayload;
import com.nabagagem.connectbe.domain.job.JobPayload;
import com.nabagagem.connectbe.domain.job.JobRequiredAvailability;
import com.nabagagem.connectbe.domain.job.JobSize;
import com.nabagagem.connectbe.domain.job.JobStatus;
import com.nabagagem.connectbe.entities.DateInterval;
import com.nabagagem.connectbe.entities.Job;
import com.nabagagem.connectbe.entities.MoneyAmount;
import com.nabagagem.connectbe.services.jobs.JobAuthService;
import com.nabagagem.connectbe.services.jobs.JobService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(JobController.class)
@WithMockUser("cc260c3b-56a1-4cbc-9b95-d5f542cd3bc7")
class JobControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JobAuthService mockJobAuthService;
    @MockBean
    private JobService mockJobService;

    @Autowired
    private ObjectMapper objectMapper;
    @Captor
    private ArgumentCaptor<JobPayload> argumentCaptor;

    @Test
    void testPost() throws Exception {
        // Setup
        // Configure JobService.create(...).
        final Job job = Job.builder()
                .id(UUID.fromString("70c2e2f6-69b7-456d-bb63-6d1a4279b53a"))
                .build();
        final MoneyAmount moneyAmount = new MoneyAmount();
        moneyAmount.setAmount(new BigDecimal("0.00"));
        moneyAmount.setCurrency(MoneyAmount.MoneyCurrency.EUR);
        final DateInterval dateInterval = new DateInterval();
        dateInterval.setStartAt(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC));
        dateInterval.setFinishAt(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC));
        final JobPayload jobPayload = new JobPayload(null,
                null, "title", moneyAmount, JobCategory.IT,
                "description", JobSize.S, JobFrequency.ONE_SHOT, "background", JobMode.PRESENCE,
                JobRequiredAvailability.SOON, dateInterval, "address", "addressReference", Set.of("value"),
                JobStatus.PUBLISHED, Set.of("value"));
        UUID loggedUserId = UUID.fromString("cc260c3b-56a1-4cbc-9b95-d5f542cd3bc7");
        when(mockJobService.create(any(JobPayload.class), eq(loggedUserId)))
                .thenReturn(job);

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(post("/api/v1/jobs")
                        .content(objectMapper.writeValueAsString(jobPayload))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("{\"id\":\"70c2e2f6-69b7-456d-bb63-6d1a4279b53a\"}");
        verify(mockJobService).create(argumentCaptor.capture(), eq(loggedUserId));
        assertThat(argumentCaptor.getValue())
                .usingRecursiveComparison()
                .ignoringFields("requiredDates.finishAt")
                .ignoringFields("requiredDates.startAt")
                .isEqualTo(jobPayload);
    }

    @Test
    void testUpdate() throws Exception {
        // Setup
        final MoneyAmount moneyAmount = new MoneyAmount();
        moneyAmount.setAmount(new BigDecimal("0.00"));
        moneyAmount.setCurrency(MoneyAmount.MoneyCurrency.EUR);
        final DateInterval dateInterval = new DateInterval();
        dateInterval.setStartAt(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC));
        dateInterval.setFinishAt(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC));
        final JobPayload jobPayload = new JobPayload(null,
                null, "title", moneyAmount, JobCategory.IT,
                "description", JobSize.S, JobFrequency.ONE_SHOT, "background", JobMode.PRESENCE,
                JobRequiredAvailability.SOON, dateInterval, "address", "addressReference", Set.of("value"),
                JobStatus.PUBLISHED, Set.of("value"));
        UUID jobId = UUID.fromString("bd53ee3d-addf-4164-8cf8-454eb2ccf62c");
        doNothing().when(mockJobService)
                .update(eq(jobId), any());

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(
                        put("/api/v1/jobs/{id}", "bd53ee3d-addf-4164-8cf8-454eb2ccf62c")
                                .with(csrf())
                                .content(objectMapper.writeValueAsString(jobPayload))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        verify(mockJobAuthService).failIfUnauthorized(jobId);

        // Confirm JobService.update(...).
        verify(mockJobService).update(eq(jobId), argumentCaptor.capture());
        assertThat(argumentCaptor.getValue())
                .usingRecursiveComparison()
                .ignoringFields("requiredDates.finishAt")
                .ignoringFields("requiredDates.startAt")
                .isEqualTo(jobPayload);
    }

    @Test
    void testPatch() throws Exception {
        // Setup
        // Run the test
        JobPatchPayload jobPatchPayload = new JobPatchPayload(JobStatus.PUBLISHED);
        final MockHttpServletResponse response = mockMvc.perform(
                        patch("/api/v1/jobs/{id}", "d7e7fcff-4b97-4e96-8da1-ba946369e192")
                                .with(csrf())
                                .content(objectMapper.writeValueAsString(jobPatchPayload))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        verify(mockJobAuthService).failIfUnauthorized(UUID.fromString("d7e7fcff-4b97-4e96-8da1-ba946369e192"));
        verify(mockJobService).patch(UUID.fromString("d7e7fcff-4b97-4e96-8da1-ba946369e192"),
                jobPatchPayload);
    }

    @Test
    void testGet() throws Exception {
        // Setup
        // Configure JobService.getJob(...).
        final MoneyAmount moneyAmount = new MoneyAmount();
        moneyAmount.setAmount(new BigDecimal("0.00"));
        moneyAmount.setCurrency(MoneyAmount.MoneyCurrency.EUR);
        final DateInterval dateInterval = new DateInterval();
        dateInterval.setStartAt(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC));
        dateInterval.setFinishAt(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC));
        final Optional<JobPayload> jobPayload = Optional.of(
                new JobPayload(UUID.fromString("cc8bcc22-d6b3-4c21-9d9d-ea97aa9ff9d6"),
                        UUID.fromString("1bc2aafa-2c21-43f9-bc9a-77d35fd12b79"), "title", moneyAmount, JobCategory.IT,
                        "description", JobSize.S, JobFrequency.ONE_SHOT, "background", JobMode.PRESENCE,
                        JobRequiredAvailability.SOON, dateInterval, "address", "addressReference", Set.of("value"),
                        JobStatus.PUBLISHED, Set.of("value")));
        when(mockJobService.getJob("id")).thenReturn(jobPayload);

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(get("/api/v1/jobs/{id}", "id")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThatJson(response.getContentAsString()).isEqualTo("""
                {"ownerId":"cc8bcc22-d6b3-4c21-9d9d-ea97aa9ff9d6","id":"1bc2aafa-2c21-43f9-bc9a-77d35fd12b79",
                "title":"title","budget":{"amount":0.00,"currency":"EUR"},
                "jobCategory":"IT","description":"description","jobSize":"S",
                "jobFrequency":"ONE_SHOT","background":"background","jobMode":"PRESENCE",
                "requiredAvailability":"SOON","requiredDates":{"startAt":"2020-01-01T00:00:00Z",
                "finishAt":"2020-01-01T00:00:00Z"},"address":"address","addressReference":"addressReference",
                "requiredSkills":["value"],"jobStatus":"PUBLISHED","tags":["value"]}
                """);
    }

    @Test
    void testGet_JobServiceReturnsAbsent() throws Exception {
        // Setup
        when(mockJobService.getJob("id")).thenReturn(Optional.empty());

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(get("/api/v1/jobs/{id}", "id")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void testDelete() throws Exception {
        // Setup
        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(
                        delete("/api/v1/jobs/{id}", "b38fdd2d-5144-472f-a23a-7ed6d5dca322")
                                .with(csrf())
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        verify(mockJobAuthService).failIfUnauthorized(UUID.fromString("b38fdd2d-5144-472f-a23a-7ed6d5dca322"));
        verify(mockJobService).delete(UUID.fromString("b38fdd2d-5144-472f-a23a-7ed6d5dca322"));
    }
}
