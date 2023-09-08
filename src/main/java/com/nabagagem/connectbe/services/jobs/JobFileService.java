package com.nabagagem.connectbe.services.jobs;

import com.nabagagem.connectbe.domain.job.DeleteJobFileCommand;
import com.nabagagem.connectbe.domain.job.GetJobMediaCommand;
import com.nabagagem.connectbe.domain.job.JobFileCommand;
import com.nabagagem.connectbe.domain.messages.JobMediaInfo;
import com.nabagagem.connectbe.entities.Job;
import com.nabagagem.connectbe.entities.JobMedia;
import com.nabagagem.connectbe.entities.Media;
import com.nabagagem.connectbe.repos.JobMediaRepository;
import com.nabagagem.connectbe.services.MediaService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
public class JobFileService {
    private final JobMediaRepository jobMediaRepository;
    private final MediaService mediaService;

    public void create(JobFileCommand jobFileCommand) {
        jobMediaRepository.save(
                JobMedia.builder()
                        .job(Job.builder().id(jobFileCommand.jobId()).build())
                        .media(mediaService.upload(jobFileCommand.file()))
                        .filePurpose(jobFileCommand.filePurpose())
                        .position(
                                Optional.ofNullable(jobFileCommand.position())
                                        .orElse(0)
                        ).build()
        );
    }

    public Set<JobMediaInfo> listFor(UUID jobId) {
        return jobMediaRepository.listForJobId(jobId);
    }

    public Optional<Media> getMediaFrom(GetJobMediaCommand getJobMediaCommand) {
        return jobMediaRepository.findMediaFrom(
                getJobMediaCommand.jobId(),
                getJobMediaCommand.filePurpose(),
                getJobMediaCommand.position()
        );
    }

    public void delete(DeleteJobFileCommand deleteJobFileCommand) {
        jobMediaRepository.findFrom(
                        deleteJobFileCommand.jobId(),
                        deleteJobFileCommand.filePurpose(),
                        deleteJobFileCommand.position())
                .stream()
                .peek(jobMediaRepository::delete)
                .map(JobMedia::getMedia)
                .forEach(mediaService::delete);
    }

    public void deleteAllForJob(UUID id) {
        jobMediaRepository.findFromJob(id)
                .stream()
                .peek(jobMediaRepository::delete)
                .map(JobMedia::getMedia)
                .forEach(mediaService::delete);
    }
}
