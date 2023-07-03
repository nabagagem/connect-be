package com.nabagagem.connectbe.services;

import com.nabagagem.connectbe.entities.Job;
import com.nabagagem.connectbe.entities.Skill;
import com.nabagagem.connectbe.services.search.KeywordService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class JobIndexService {
    private final KeywordService keywordService;

    public Set<String> extractFrom(Job job) {
        String fullText = String.format(
                "%s %s %s %s %s %s %s %s %s %s %s %s",
                job.getTags(),
                job.getJobSize(),
                job.getJobMode(),
                job.getJobFrequency(),
                job.getJobStatus(),
                job.getJobCategory(),
                job.getAddress(),
                job.getAddressReference(),
                job.getBackground(),
                job.getDescription(),
                job.getRequiredSkills().stream().map(Skill::getName).collect(Collectors.toSet()),
                job.getTitle()
        );
        log.info("Job full text: {}", fullText);
        return keywordService.extractFrom(fullText);
    }
}
