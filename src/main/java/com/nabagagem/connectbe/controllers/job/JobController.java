package com.nabagagem.connectbe.controllers.job;

import com.nabagagem.connectbe.domain.JobPayload;
import com.nabagagem.connectbe.domain.ResourceRef;
import com.nabagagem.connectbe.services.JobService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/jobs")
public class JobController {
    private final JobService jobService;

    @PostMapping
    public ResourceRef post(@RequestBody @Valid JobPayload jobPayload,
                            Principal principal) {
        return new ResourceRef(jobService.create(jobPayload,
                UUID.fromString(principal.getName())).getId().toString());
    }

    @PutMapping("/{id}")
    public void update(@RequestBody @Valid JobPayload jobPayload,
                       @PathVariable UUID id) {
        jobService.update(id, jobPayload);
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobPayload> get(@PathVariable String id) {
        return jobService.getJob(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        jobService.delete(id);
    }
}
