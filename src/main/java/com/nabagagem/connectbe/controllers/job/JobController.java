package com.nabagagem.connectbe.controllers.job;

import com.nabagagem.connectbe.domain.ResourceRef;
import com.nabagagem.connectbe.domain.job.JobPatchPayload;
import com.nabagagem.connectbe.domain.job.JobPayload;
import com.nabagagem.connectbe.services.jobs.JobService;
import com.nabagagem.connectbe.services.profile.UserInfoService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/jobs")
public class JobController {
    private final JobService jobService;
    private final UserInfoService userInfoService;

    @PostMapping
    public ResourceRef post(@RequestBody @Valid JobPayload jobPayload) {
        return new ResourceRef(jobService.create(jobPayload,
                userInfoService.getCurrentUserInfo(null).userId()));
    }

    @PutMapping("/{id}")
    public void update(@RequestBody @Valid JobPayload jobPayload,
                       @PathVariable UUID id) {
        //jobAuthService.failIfUnauthorized(id);
        jobService.update(id, jobPayload);
    }

    @PatchMapping("/{id}")
    public void patch(@RequestBody @Valid JobPatchPayload jobPatchPayload,
                      @PathVariable UUID id) {
        //jobAuthService.failIfUnauthorized(id);
        jobService.patch(id, jobPatchPayload);
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobPayload> get(@PathVariable String id) {
        return jobService.getJob(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        //jobAuthService.failIfUnauthorized(id);
        jobService.delete(id);
    }
}
