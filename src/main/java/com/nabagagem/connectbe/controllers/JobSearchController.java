package com.nabagagem.connectbe.controllers;

import com.nabagagem.connectbe.domain.JobSearchItem;
import com.nabagagem.connectbe.domain.JobSearchParams;
import com.nabagagem.connectbe.services.JobMapper;
import com.nabagagem.connectbe.services.JobSearchService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@Slf4j
public class JobSearchController {
    private final JobSearchService jobSearchService;
    private final JobMapper jobMapper;

    @GetMapping("/api/v1/jobs")
    public List<JobSearchItem> get(JobSearchParams jobSearchParams,
                                   Pageable pageable) {
        log.info("Search params: {}", jobSearchParams);
        return jobSearchService.search(jobSearchParams, pageable)
                .stream()
                .map(jobMapper::toSearchItem).collect(Collectors.toList());
    }
}
