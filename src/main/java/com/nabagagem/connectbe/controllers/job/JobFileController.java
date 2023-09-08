package com.nabagagem.connectbe.controllers.job;

import com.nabagagem.connectbe.controllers.MediaControllerHelper;
import com.nabagagem.connectbe.domain.FilePurpose;
import com.nabagagem.connectbe.domain.job.DeleteJobFileCommand;
import com.nabagagem.connectbe.domain.job.GetJobMediaCommand;
import com.nabagagem.connectbe.domain.job.JobFileCommand;
import com.nabagagem.connectbe.domain.messages.JobMediaInfo;
import com.nabagagem.connectbe.services.jobs.JobAuthService;
import com.nabagagem.connectbe.services.jobs.JobFileService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/jobs/{jobId}/files")
public class JobFileController {
    private final JobFileService jobFileService;
    private final MediaControllerHelper mediaControllerHelper;
    private final JobAuthService jobAuthService;

    @PutMapping(
            value = "/{filePurpose}/{position}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void uploadOnPosition(@PathVariable UUID jobId,
                                 @PathVariable FilePurpose filePurpose,
                                 @PathVariable Integer position,
                                 @RequestParam MultipartFile file) {
        jobAuthService.failIfUnauthorized(jobId);
        jobFileService.create(new JobFileCommand(jobId, filePurpose, position, file));
    }

    @PutMapping(
            value = "/{filePurpose}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void upload(@PathVariable UUID jobId,
                       @PathVariable FilePurpose filePurpose,
                       @RequestParam MultipartFile file) {
        jobAuthService.failIfUnauthorized(jobId);
        jobFileService.create(new JobFileCommand(jobId, filePurpose, 0, file));
    }

    @GetMapping
    public Set<JobMediaInfo> list(@PathVariable UUID jobId) {
        return jobFileService.listFor(jobId);
    }

    @GetMapping("/{filePurpose}/{position}")
    public ResponseEntity<byte[]> getJobMediaOnPosition(@PathVariable UUID jobId,
                                                        @PathVariable FilePurpose filePurpose,
                                                        @PathVariable Integer position) {
        return jobFileService.getMediaFrom(new GetJobMediaCommand(jobId, filePurpose, position))
                .map(mediaControllerHelper::toResponse)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{filePurpose}/{position}")
    public void deleteOnPosition(@PathVariable UUID jobId,
                                 @PathVariable FilePurpose filePurpose,
                                 @PathVariable Integer position) {
        jobAuthService.failIfUnauthorized(jobId);
        jobFileService.delete(new DeleteJobFileCommand(jobId, filePurpose, position));
    }

    @DeleteMapping("/{filePurpose}")
    public void delete(@PathVariable UUID jobId,
                       @PathVariable FilePurpose filePurpose) {
        jobAuthService.failIfUnauthorized(jobId);
        jobFileService.delete(new DeleteJobFileCommand(jobId, filePurpose, 0));
    }

    @GetMapping("/{filePurpose}")
    public ResponseEntity<byte[]> getJobMedia(@PathVariable UUID jobId,
                                              @PathVariable FilePurpose filePurpose) {
        return jobFileService.getMediaFrom(new GetJobMediaCommand(jobId, filePurpose, 0))
                .map(mediaControllerHelper::toResponse)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
