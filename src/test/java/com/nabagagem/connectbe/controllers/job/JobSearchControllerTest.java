package com.nabagagem.connectbe.controllers.job;

import com.nabagagem.connectbe.domain.job.JobCategory;
import com.nabagagem.connectbe.domain.job.JobFrequency;
import com.nabagagem.connectbe.domain.job.JobRequiredAvailability;
import com.nabagagem.connectbe.domain.job.JobSearchItem;
import com.nabagagem.connectbe.domain.job.JobSearchParams;
import com.nabagagem.connectbe.domain.job.JobSize;
import com.nabagagem.connectbe.domain.job.JobStatus;
import com.nabagagem.connectbe.domain.job.ProfileJobItem;
import com.nabagagem.connectbe.domain.profile.WorkingMode;
import com.nabagagem.connectbe.entities.DateInterval;
import com.nabagagem.connectbe.entities.Job;
import com.nabagagem.connectbe.entities.MoneyAmount;
import com.nabagagem.connectbe.services.jobs.JobMapper;
import com.nabagagem.connectbe.services.jobs.JobSearchService;
import com.nabagagem.connectbe.services.profile.ProfileAuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@ExtendWith(SpringExtension.class)
@WebMvcTest(JobSearchController.class)
@WithMockUser("ab27b2fe-2bd7-420f-8f71-a26f260cdb77")
class JobSearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JobSearchService mockJobSearchService;
    @MockBean
    private JobMapper mockJobMapper;
    @MockBean
    private ProfileAuthService mockProfileAuthService;

    @Test
    void testGet() throws Exception {
        // Setup
        // Configure JobSearchService.search(...).
        final Page<Job> jobs = new PageImpl<>(List.of(Job.builder().build()));
        when(mockJobSearchService.search(
                eq(new JobSearchParams(
                        Set.of(JobCategory.IT),
                        Set.of(JobSize.S),
                        Set.of(JobFrequency.ONE_SHOT),
                        Set.of(WorkingMode.ONSITE),
                        Set.of(JobRequiredAvailability.SOON), Set.of("skill"),
                        Set.of(JobStatus.PUBLISHED), null,
                        null,
                        null,
                        null, "search")),
                eq(UUID.fromString("ab27b2fe-2bd7-420f-8f71-a26f260cdb77")), any(Pageable.class))).thenReturn(jobs);

        // Configure JobMapper.toSearchItem(...).
        final MoneyAmount moneyAmount = new MoneyAmount();
        moneyAmount.setAmount(new BigDecimal("0.00"));
        moneyAmount.setCurrency(MoneyAmount.MoneyCurrency.EUR);
        final DateInterval dateInterval = new DateInterval();
        dateInterval.setStartAt(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC));
        dateInterval.setFinishAt(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC));
        final DateInterval dateInterval1 = new DateInterval();
        dateInterval1.setStartAt(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC));
        dateInterval1.setFinishAt(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC));
        final JobSearchItem jobSearchItem = new JobSearchItem(UUID.fromString("7455162d-4d43-459f-9ff2-0f5184cc4ff4"),
                new ProfileJobItem("id", "publicName", true), "title", moneyAmount, JobCategory.IT, "description", JobSize.S,
                JobFrequency.ONE_SHOT, "background", WorkingMode.BOTH, JobRequiredAvailability.SOON, dateInterval,
                "address", "addressReference", Set.of("value"), JobStatus.PUBLISHED, Set.of("value"), dateInterval1,
                ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC));
        when(mockJobMapper.toSearchItem(Job.builder().build())).thenReturn(jobSearchItem);

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(get("/api/v1/jobs")
                        .accept(MediaType.APPLICATION_JSON)
                        .param("jobCategories", JobCategory.IT.toString())
                        .param("jobSize", JobSize.S.toString())
                        .param("jobFrequencies", JobFrequency.ONE_SHOT.toString())
                        .param("jobModes", WorkingMode.ONSITE.toString())
                        .param("requiredAvailabilities", JobRequiredAvailability.SOON.toString())
                        .param("requiredSkills", "skill")
                        .param("jobStatuses", JobStatus.PUBLISHED.toString())
                        .param("searchExpression", "search")
                )
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThatJson(response.getContentAsString()).isEqualTo("""
                {"content":[{"id":"7455162d-4d43-459f-9ff2-0f5184cc4ff4","profile":{"id":"id","publicName":"publicName","publicProfile":true},
                "title":"title","budget":{"amount":0.00,"currency":"EUR"},"jobCategory":"IT","description":"description","jobSize":"S",
                "jobFrequency":"ONE_SHOT","background":"background","jobMode":"BOTH","requiredAvailability":"SOON",
                "dateInterval":{"startAt":"2020-01-01T00:00:00Z","finishAt":"2020-01-01T00:00:00Z"},"address":"address",
                "addressReference":"addressReference","requiredSkills":["value"],"jobStatus":"PUBLISHED","tags":["value"],
                "requiredDates":{"startAt":"2020-01-01T00:00:00Z","finishAt":"2020-01-01T00:00:00Z"},"createdAt":"2020-01-01T00:00:00Z"}],
                "pageable":"INSTANCE","totalPages":1,"totalElements":1,"last":true,"size":1,"number":0,"sort":{"empty":true,"sorted":false,"unsorted":true},
                "numberOfElements":1,"first":true,"empty":false}
                """);
    }

    @Test
    void testGet_JobSearchServiceReturnsNoItems() throws Exception {
        // Setup
        when(mockJobSearchService.search(
                any(),
                eq(UUID.fromString("ab27b2fe-2bd7-420f-8f71-a26f260cdb77")), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        // Configure JobMapper.toSearchItem(...).

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(get("/api/v1/jobs")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThatJson(response.getContentAsString()).isEqualTo("""
                {"content":[],"pageable":"INSTANCE","last":true,"totalElements":0,
                "totalPages":1,"size":0,"number":0,"sort":{"empty":true,"sorted":false,"unsorted":true},
                "first":true,"numberOfElements":0,"empty":true}
                """);
    }

    @Test
    void testGetByProfile() throws Exception {
        // Setup
        // Configure JobSearchService.search(...).
        final Page<Job> jobs = new PageImpl<>(List.of(Job.builder().build()));
        when(mockJobSearchService.search(eq(UUID.fromString("df9334ce-313c-4543-b07a-f9d25fce4593")),
                any(),
                any(Pageable.class))).thenReturn(jobs);

        // Configure JobMapper.toSearchItem(...).
        final MoneyAmount moneyAmount = new MoneyAmount();
        moneyAmount.setAmount(new BigDecimal("0.00"));
        moneyAmount.setCurrency(MoneyAmount.MoneyCurrency.EUR);
        final DateInterval dateInterval = new DateInterval();
        dateInterval.setStartAt(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC));
        dateInterval.setFinishAt(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC));
        final DateInterval dateInterval1 = new DateInterval();
        dateInterval1.setStartAt(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC));
        dateInterval1.setFinishAt(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC));
        final JobSearchItem jobSearchItem = new JobSearchItem(UUID.fromString("a2ba245f-a41d-4687-a17a-e3d0b3def16f"),
                new ProfileJobItem("id", "publicName", true), "title", moneyAmount, JobCategory.IT, "description", JobSize.S,
                JobFrequency.ONE_SHOT, "background", WorkingMode.ONSITE, JobRequiredAvailability.SOON, dateInterval,
                "address", "addressReference", Set.of("value"), JobStatus.PUBLISHED, Set.of("value"), dateInterval1,
                ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC));
        when(mockJobMapper.toSearchItem(Job.builder().build())).thenReturn(jobSearchItem);

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(
                        get("/api/v1/profile/{profileId}/jobs", "df9334ce-313c-4543-b07a-f9d25fce4593")
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThatJson(response.getContentAsString()).isEqualTo("""
                {"content":[{"id":"a2ba245f-a41d-4687-a17a-e3d0b3def16f","profile":{"id":"id","publicName":"publicName","publicProfile":true},
                "title":"title","budget":{"amount":0.00,"currency":"EUR"},"jobCategory":"IT","description":"description","jobSize":"S",
                "jobFrequency":"ONE_SHOT","background":"background","jobMode":"ONSITE","requiredAvailability":"SOON",
                "dateInterval":{"startAt":"2020-01-01T00:00:00Z","finishAt":"2020-01-01T00:00:00Z"},"address":"address","addressReference":"addressReference",
                "requiredSkills":["value"],"jobStatus":"PUBLISHED","tags":["value"],"requiredDates":{"startAt":"2020-01-01T00:00:00Z",
                "finishAt":"2020-01-01T00:00:00Z"},"createdAt":"2020-01-01T00:00:00Z"}],"pageable":"INSTANCE","totalPages":1,"totalElements":1,"last":true,
                "size":1,"number":0,"sort":{"empty":true,"sorted":false,"unsorted":true},"numberOfElements":1,"first":true,"empty":false}
                """);
        verify(mockProfileAuthService).failIfNotLoggedIn(UUID.fromString("df9334ce-313c-4543-b07a-f9d25fce4593"));
    }

    @Test
    void testGetByProfile_JobSearchServiceReturnsNoItems() throws Exception {
        // Setup
        when(mockJobSearchService.search(eq(UUID.fromString("df9334ce-313c-4543-b07a-f9d25fce4593")),
                any(),
                any(Pageable.class))).thenReturn(new PageImpl<>(Collections.emptyList()));

        // Configure JobMapper.toSearchItem(...).
        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(
                        get("/api/v1/profile/{profileId}/jobs", "df9334ce-313c-4543-b07a-f9d25fce4593")
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThatJson(response.getContentAsString()).isEqualTo("""
                {"content":[],"pageable":"INSTANCE","last":true,"totalElements":0,"totalPages":1,
                "size":0,"number":0,"sort":{"empty":true,"sorted":false,"unsorted":true},"first":true,"numberOfElements":0,"empty":true}
                """);
        verify(mockProfileAuthService).failIfNotLoggedIn(UUID.fromString("df9334ce-313c-4543-b07a-f9d25fce4593"));
    }
}
