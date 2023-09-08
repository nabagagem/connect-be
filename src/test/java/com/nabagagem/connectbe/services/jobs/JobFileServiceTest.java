package com.nabagagem.connectbe.services.jobs;

import com.nabagagem.connectbe.domain.FilePurpose;
import com.nabagagem.connectbe.domain.job.DeleteJobFileCommand;
import com.nabagagem.connectbe.domain.job.GetJobMediaCommand;
import com.nabagagem.connectbe.domain.job.JobFileCommand;
import com.nabagagem.connectbe.domain.messages.JobMediaInfo;
import com.nabagagem.connectbe.entities.Job;
import com.nabagagem.connectbe.entities.JobMedia;
import com.nabagagem.connectbe.entities.Media;
import com.nabagagem.connectbe.repos.JobMediaRepository;
import com.nabagagem.connectbe.services.MediaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JobFileServiceTest {

    @Mock
    private JobMediaRepository mockJobMediaRepository;
    @Mock
    private MediaService mockMediaService;

    private JobFileService jobFileServiceUnderTest;

    @BeforeEach
    void setUp() {
        jobFileServiceUnderTest = new JobFileService(mockJobMediaRepository, mockMediaService);
    }

    @Test
    void testCreate() {
        // Setup
        ArgumentCaptor<JobMedia> jobArgumentCaptor = ArgumentCaptor.forClass(JobMedia.class);
        UUID jobId = UUID.fromString("aaaedf8e-842a-43b9-9e51-7a99bfc30efc");
        final JobFileCommand jobFileCommand = new JobFileCommand(
                jobId, FilePurpose.PIC, 0,
                new MockMultipartFile("name", "content".getBytes()));
        Media media = Media.builder()
                .id(UUID.randomUUID())
                .build();
        when(mockMediaService.upload(any(MultipartFile.class))).thenReturn(media);

        // Run the test
        jobFileServiceUnderTest.create(jobFileCommand);

        // Verify the results
        verify(mockJobMediaRepository).save(jobArgumentCaptor.capture());
        assertThat(jobArgumentCaptor.getValue())
                .hasFieldOrPropertyWithValue("job.id", jobId)
                .hasFieldOrPropertyWithValue("media", media)
                .hasFieldOrPropertyWithValue("filePurpose", FilePurpose.PIC)
                .hasFieldOrPropertyWithValue("position", 0);
    }

    @Test
    void testCreate_JobMediaRepositoryThrowsOptimisticLockingFailureException() {
        // Setup
        final JobFileCommand jobFileCommand = new JobFileCommand(
                UUID.fromString("aaaedf8e-842a-43b9-9e51-7a99bfc30efc"), FilePurpose.PIC, 0,
                new MockMultipartFile("name", "content".getBytes()));
        when(mockMediaService.upload(any(MultipartFile.class))).thenReturn(Media.builder().build());
        when(mockJobMediaRepository.save(any(JobMedia.class))).thenThrow(OptimisticLockingFailureException.class);

        // Run the test
        assertThatThrownBy(() -> jobFileServiceUnderTest.create(jobFileCommand))
                .isInstanceOf(OptimisticLockingFailureException.class);
    }

    @Test
    void testListFor_JobMediaRepositoryReturnsNoItems() {
        // Setup
        when(mockJobMediaRepository.listForJobId(UUID.fromString("75dd738a-5391-41f0-abda-f2304e162a31")))
                .thenReturn(Collections.emptySet());

        // Run the test
        final Set<JobMediaInfo> result = jobFileServiceUnderTest.listFor(
                UUID.fromString("75dd738a-5391-41f0-abda-f2304e162a31"));

        // Verify the results
        assertThat(result).isEqualTo(Collections.emptySet());
    }

    @Test
    void testGetMediaFrom() {
        // Setup
        UUID jobId = UUID.fromString("45fd5e7f-8012-4d6f-90c6-07dab8a08d60");
        final GetJobMediaCommand getJobMediaCommand = new GetJobMediaCommand(
                jobId, FilePurpose.PIC, 0);
        final Optional<Media> expectedResult = Optional.of(Media.builder().build());
        when(mockJobMediaRepository.findMediaFrom(jobId,
                FilePurpose.PIC, 0)).thenReturn(Optional.of(Media.builder().build()));

        // Run the test
        final Optional<Media> result = jobFileServiceUnderTest.getMediaFrom(getJobMediaCommand);

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testGetMediaFrom_JobMediaRepositoryReturnsAbsent() {
        // Setup
        UUID jobId = UUID.fromString("45fd5e7f-8012-4d6f-90c6-07dab8a08d60");
        final GetJobMediaCommand getJobMediaCommand = new GetJobMediaCommand(
                jobId, FilePurpose.PIC, 0);
        when(mockJobMediaRepository.findMediaFrom(jobId,
                FilePurpose.PIC, 0)).thenReturn(Optional.empty());

        // Run the test
        final Optional<Media> result = jobFileServiceUnderTest.getMediaFrom(getJobMediaCommand);

        // Verify the results
        assertThat(result).isEmpty();
    }

    @Test
    void testDelete() {
        // Setup
        UUID jobId = UUID.fromString("f08d69eb-af15-40aa-ad8e-7fbe6971eace");
        final DeleteJobFileCommand deleteJobFileCommand = new DeleteJobFileCommand(
                jobId, FilePurpose.PIC, 0);

        // Configure JobMediaRepository.findFrom(...).
        final Optional<JobMedia> jobMedia = Optional.of(JobMedia.builder()
                .job(Job.builder()
                        .id(jobId)
                        .build())
                .media(Media.builder().build())
                .filePurpose(FilePurpose.PIC)
                .position(0)
                .build());
        when(mockJobMediaRepository.findFrom(jobId, FilePurpose.PIC,
                0)).thenReturn(jobMedia);

        // Run the test
        jobFileServiceUnderTest.delete(deleteJobFileCommand);

        // Verify the results
        verify(mockJobMediaRepository).delete(jobMedia.get());
        verify(mockMediaService).delete(Media.builder().build());
    }

    @Test
    void testDelete_JobMediaRepositoryFindFromReturnsAbsent() {
        // Setup
        UUID jobId = UUID.fromString("f08d69eb-af15-40aa-ad8e-7fbe6971eace");
        final DeleteJobFileCommand deleteJobFileCommand = new DeleteJobFileCommand(
                jobId, FilePurpose.PIC, 0);
        when(mockJobMediaRepository.findFrom(jobId, FilePurpose.PIC,
                0)).thenReturn(Optional.empty());

        // Run the test
        jobFileServiceUnderTest.delete(deleteJobFileCommand);

        // Verify the results
    }
}
