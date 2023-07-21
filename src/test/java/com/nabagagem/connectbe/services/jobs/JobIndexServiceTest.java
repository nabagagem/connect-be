package com.nabagagem.connectbe.services.jobs;

import com.nabagagem.connectbe.config.TranslationsConfig;
import com.nabagagem.connectbe.domain.job.JobCategory;
import com.nabagagem.connectbe.domain.job.JobFrequency;
import com.nabagagem.connectbe.domain.job.JobMode;
import com.nabagagem.connectbe.domain.job.JobSize;
import com.nabagagem.connectbe.domain.job.JobStatus;
import com.nabagagem.connectbe.entities.Job;
import com.nabagagem.connectbe.entities.Skill;
import com.nabagagem.connectbe.services.search.KeywordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class JobIndexServiceTest {

    @Spy
    private KeywordService mockKeywordService = new KeywordService();
    @Spy
    private MessageSource mockMessageSource = new TranslationsConfig().messageSource();

    private JobIndexService jobIndexServiceUnderTest;

    @BeforeEach
    void setUp() {
        jobIndexServiceUnderTest = new JobIndexService(mockKeywordService, mockMessageSource);
    }

    @Test
    void testExtractFrom() {
        // Setup
        final Job job = Job.builder()
                .title("title")
                .jobCategory(JobCategory.IT)
                .description("description")
                .jobSize(JobSize.S)
                .jobFrequency(JobFrequency.ONE_SHOT)
                .background("background")
                .jobMode(JobMode.PRESENCE)
                .address("address")
                .addressReference("addressReference")
                .requiredSkills(Set.of(Skill.builder()
                        .name("name")
                        .build()))
                .jobStatus(JobStatus.PUBLISHED)
                .tags(Set.of("value"))
                .build();
        // Run the test
        final Set<String> result = jobIndexServiceUnderTest.extractFrom(job);

        // Verify the results
        assertThat(result).isEqualTo(Set.of("pequen",
                "valu",
                "addres",
                "description",
                "one_shot",
                "programaca",
                "published",
                "it",
                "presenc",
                "s",
                "pontual",
                "apen",
                "ti",
                "publicad",
                "background",
                "name",
                "addressreferenc",
                "titl",
                "presencial",
                "trabalh"));
    }
}
