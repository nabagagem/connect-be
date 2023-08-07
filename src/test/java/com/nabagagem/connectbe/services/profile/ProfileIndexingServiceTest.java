package com.nabagagem.connectbe.services.profile;

import com.nabagagem.connectbe.domain.job.JobCategory;
import com.nabagagem.connectbe.domain.profile.WorkingMode;
import com.nabagagem.connectbe.entities.Certification;
import com.nabagagem.connectbe.entities.ConnectProfile;
import com.nabagagem.connectbe.entities.PersonalInfo;
import com.nabagagem.connectbe.entities.ProfileSkill;
import com.nabagagem.connectbe.entities.Skill;
import com.nabagagem.connectbe.services.search.KeywordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProfileIndexingServiceTest {

    @Mock
    private KeywordService mockKeywordService;
    @Mock
    private MessageSource mockMessageSource;

    private ProfileIndexingService profileIndexingServiceUnderTest;

    @BeforeEach
    void setUp() throws Exception {
        profileIndexingServiceUnderTest = new ProfileIndexingService(mockKeywordService, mockMessageSource);
    }

    @Test
    void testExtractFrom() {
        // Setup
        final ConnectProfile profile = ConnectProfile.builder()
                .id(UUID.fromString("80048179-1a05-483d-9c24-15f3576c50af"))
                .personalInfo(PersonalInfo.builder()
                        .profession("profession")
                        .highlightTitle("highlightTitle")
                        .profileCategory(JobCategory.IT)
                        .workingMode(WorkingMode.REMOTE)
                        .city("city")
                        .tags(Set.of("value"))
                        .build())
                .profileSkills(Set.of(ProfileSkill.builder()
                        .skill(Skill.builder()
                                .name("name")
                                .build())
                        .build()))
                .certifications(Set.of(Certification.builder()
                        .title("title")
                        .build()))
                .build();
        when(mockMessageSource.getMessage(
                any(), any(), any()))
                .thenReturn("result")
                .thenReturn("result1")
                .thenReturn("result2")
        ;
        when(mockKeywordService.extractFrom("[name] highlightTitle IT result [value] REMOTE result1 city profession [title]")).thenReturn(Set.of("value"));

        // Run the test
        final Set<String> result = profileIndexingServiceUnderTest.extractFrom(profile);

        // Verify the results
        assertThat(result).isEqualTo(Set.of("value"));
    }
}
