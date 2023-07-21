package com.nabagagem.connectbe.services.jobs;

import com.nabagagem.connectbe.entities.Job;
import com.nabagagem.connectbe.entities.Skill;
import com.nabagagem.connectbe.services.search.KeywordService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class JobIndexService {
    private final KeywordService keywordService;
    private final MessageSource messageSource;

    public Set<String> extractFrom(Job job) {
        Locale ptBr = Locale.forLanguageTag("pt-BR");
        String fullText = String.format(
                "%s %s %s %s %s %s %s %s %s %s %s %s %s %s %s %s %s",
                job.getTags(),
                job.getJobSize(),
                messageSource.getMessage(job.getJobSize().toString(), null, ptBr),
                job.getJobMode(),
                messageSource.getMessage(job.getJobMode().toString(), null, ptBr),
                job.getJobFrequency(),
                messageSource.getMessage(job.getJobFrequency().toString(), null, ptBr),
                job.getJobStatus(),
                messageSource.getMessage(job.getJobStatus().toString(), null, ptBr),
                job.getJobCategory(),
                messageSource.getMessage(job.getJobCategory().toString(), null, ptBr),
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
