package com.nabagagem.connectbe.controllers;

import com.nabagagem.connectbe.domain.JobPayload;
import com.nabagagem.connectbe.domain.ResourceRef;
import com.nabagagem.connectbe.services.JobService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/jobs")
public class JobController {
    private final JobService jobService;

    @PostMapping
    public ResourceRef post(@RequestBody @Valid JobPayload jobPayload) {
        return new ResourceRef(jobService.create(jobPayload).getId().toString());
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobPayload> get(@PathVariable String id) {
        return jobService.getJob(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
