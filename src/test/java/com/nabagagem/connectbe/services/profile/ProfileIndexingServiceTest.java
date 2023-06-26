package com.nabagagem.connectbe.services.profile;

import com.nabagagem.connectbe.domain.JobCategory;
import com.nabagagem.connectbe.domain.WorkingMode;
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
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProfileIndexingServiceTest {

    @Mock
    private ProfileService mockProfileService;
    @Spy
    private KeywordService mockKeywordService = new KeywordService();

    private ProfileIndexingService profileIndexingServiceUnderTest;

    @BeforeEach
    void setUp() {
        profileIndexingServiceUnderTest = new ProfileIndexingService(mockProfileService, mockKeywordService);
    }

    @Test
    void testIndex() {
        // Setup
        final ConnectProfile profile = ConnectProfile.builder()
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
                .keywords(Set.of("value"))
                .build();

        // Run the test
        profileIndexingServiceUnderTest.index(profile);

        // Verify the results
        verify(mockProfileService).save(profile);
        assertThat(profile.getKeywords())
                .containsExactlyInAnyOrder("profession", "valu", "city", "name", "remot", "it", "titl", "highlighttitl");
    }
}
