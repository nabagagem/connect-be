package com.nabagagem.connectbe.services.jobs;

import com.nabagagem.connectbe.domain.job.JobCategory;
import com.nabagagem.connectbe.domain.job.JobFrequency;
import com.nabagagem.connectbe.domain.job.JobRequiredAvailability;
import com.nabagagem.connectbe.domain.job.JobSearchParams;
import com.nabagagem.connectbe.domain.job.JobSize;
import com.nabagagem.connectbe.domain.job.JobStatus;
import com.nabagagem.connectbe.domain.profile.WorkingMode;
import com.nabagagem.connectbe.entities.Job;
import com.nabagagem.connectbe.repos.JobRepo;
import com.nabagagem.connectbe.services.search.KeywordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JobSearchServiceTest {

    @Mock
    private JobRepo mockJobRepo;
    @Mock
    private KeywordService mockKeywordService;

    private JobSearchService jobSearchServiceUnderTest;

    @BeforeEach
    void setUp() {
        jobSearchServiceUnderTest = new JobSearchService(mockJobRepo, mockKeywordService);
    }

    @Test
    void testSearch1() {
        // Setup
        final JobSearchParams jobSearchParams = new JobSearchParams(Set.of(JobCategory.IT), Set.of(JobSize.S),
                Set.of(JobFrequency.ONE_SHOT), Set.of(WorkingMode.ONSITE), Set.of(JobRequiredAvailability.SOON),
                Set.of("value"), Set.of(JobStatus.PUBLISHED), Set.of("value"),
                UUID.fromString("5953a0bd-8e9d-4da6-a9cc-92cc7f058395"),
                ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC),
                ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC), "searchExpression");
        when(mockKeywordService.extractFrom("searchExpression")).thenReturn(Set.of("search"));

        // Configure JobRepo.findIdsBy(...).
        final Page<UUID> uuids = new PageImpl<>(List.of(UUID.fromString("cee971f4-4b8b-423a-9493-2bd64d7bcaaf")));
        when(mockJobRepo.findIdsBy(
                eq(jobSearchParams),
                eq(Set.of("search")), eq(UUID.fromString("5de9cd13-e106-4759-bfbf-87ec18f93e12")),
                any(Pageable.class))).thenReturn(uuids);

        Job job = Job.builder().title("result job").build();
        when(mockJobRepo.findAndFetchByIds(
                uuids.getContent()))
                .thenReturn(List.of(job));

        // Run the test
        final Page<Job> result = jobSearchServiceUnderTest.search(jobSearchParams,
                UUID.fromString("5de9cd13-e106-4759-bfbf-87ec18f93e12"), PageRequest.of(0, 1));

        // Verify the results
        assertThat(result.getContent())
                .extracting(Job::getTitle)
                .containsExactly("result job");
    }

    @Test
    void testSearch1_KeywordServiceReturnsNoItems() {
        // Setup
        final JobSearchParams jobSearchParams = new JobSearchParams(Set.of(JobCategory.IT), Set.of(JobSize.S),
                Set.of(JobFrequency.ONE_SHOT), Set.of(WorkingMode.ONSITE), Set.of(JobRequiredAvailability.SOON),
                Set.of("value"), Set.of(JobStatus.PUBLISHED), Set.of("value"),
                UUID.fromString("5953a0bd-8e9d-4da6-a9cc-92cc7f058395"),
                ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC),
                ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC), "searchExpression");
        when(mockKeywordService.extractFrom("searchExpression")).thenReturn(Collections.emptySet());

        // Configure JobRepo.findIdsBy(...).
        final Page<UUID> uuids = new PageImpl<>(List.of(UUID.fromString("cee971f4-4b8b-423a-9493-2bd64d7bcaaf")));
        when(mockJobRepo.findIdsBy(
                eq(jobSearchParams),
                eq(Collections.emptySet()), eq(UUID.fromString("5de9cd13-e106-4759-bfbf-87ec18f93e12")),
                any(Pageable.class))).thenReturn(uuids);

        when(mockJobRepo.findAndFetchByIds(
                uuids.getContent()))
                .thenReturn(List.of(Job.builder().build()));

        // Run the test
        jobSearchServiceUnderTest.search(jobSearchParams,
                UUID.fromString("5de9cd13-e106-4759-bfbf-87ec18f93e12"), PageRequest.of(0, 1));
    }
}
