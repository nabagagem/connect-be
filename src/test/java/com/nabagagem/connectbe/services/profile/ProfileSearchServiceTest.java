package com.nabagagem.connectbe.services.profile;

import com.nabagagem.connectbe.domain.job.JobCategory;
import com.nabagagem.connectbe.domain.profile.ProfileSearchItemPayload;
import com.nabagagem.connectbe.domain.profile.ProfileSearchParams;
import com.nabagagem.connectbe.domain.profile.WorkingMode;
import com.nabagagem.connectbe.repos.ProfileRepo;
import com.nabagagem.connectbe.repos.ProfileSearchItem;
import com.nabagagem.connectbe.services.search.KeywordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProfileSearchServiceTest {

    @Mock
    private ProfileRepo mockProfileRepo;
    @Mock
    private KeywordService mockKeywordService;
    private ProfileSearchService profileSearchServiceUnderTest;

    @BeforeEach
    void setUp() {
        profileSearchServiceUnderTest = new ProfileSearchService(mockProfileRepo, mockKeywordService);
    }

    @Test
    void testSearchFor() {
        // Setup
        final ProfileSearchParams profileSearchParams = new ProfileSearchParams(Set.of(JobCategory.IT),
                Set.of(WorkingMode.REMOTE), "key word");
        Set<String> keywords = Set.of("value");
        when(mockKeywordService.extractFrom("key word")).thenReturn(keywords);
        List<String> ids = List.of("value");
        when(mockProfileRepo.searchIdsFor(eq(Set.of(WorkingMode.REMOTE)), eq(Set.of(JobCategory.IT)),
                eq(keywords), eq(false), eq(UUID.fromString("25a1b254-5257-421d-a3ac-b43dadf84ab9")),
                any(Pageable.class))).thenReturn(new PageImpl<>(ids));
        UUID profileId = UUID.fromString("25a1b254-5257-421d-a3ac-b43dadf84ab9");
        ProfileSearchItem profileSearchItem = new ProfileSearchItem() {
            @Override
            public UUID getId() {
                return profileId;
            }

            @Override
            public String getSlug() {
                return "slug";
            }

            @Override
            public String getPublicName() {
                return null;
            }

            @Override
            public Boolean getAvailable() {
                return null;
            }

            @Override
            public JobCategory getCategory() {
                return null;
            }

            @Override
            public WorkingMode getWorkingMode() {
                return null;
            }

            @Override
            public boolean getPublicProfile() {
                return false;
            }

            @Override
            public String getCity() {
                return null;
            }

            @Override
            public Instant getFirstLogin() {
                return null;
            }

            @Override
            public Long getFinishedJobs() {
                return null;
            }

            @Override
            public Long getFinishedBids() {
                return null;
            }

            @Override
            public Double getStars() {
                return null;
            }

            @Override
            public String getSkillName() {
                return null;
            }

            @Override
            public String getSkillLevel() {
                return null;
            }

            @Override
            public String getProfession() {
                return null;
            }

            @Override
            public String getHighlight() {
                return null;
            }

            @Override
            public Long getRatings() {
                return null;
            }
        };
        when(mockProfileRepo.profileSearch(ids)).thenReturn(List.of(
                profileSearchItem
        ));

        // Run the test
        final Page<ProfileSearchItemPayload> result = profileSearchServiceUnderTest.searchFor(profileSearchParams,
                UUID.fromString("25a1b254-5257-421d-a3ac-b43dadf84ab9"), PageRequest.of(0, 1));

        // Verify the results
        assertThatJson(result)
                .isEqualTo("""
                        {"content":[{"id":"25a1b254-5257-421d-a3ac-b43dadf84ab9",
                        "category":null,"slug":"slug","publicName":null,"available":null,
                        "workingMode":null,"publicProfile":false,"city":null,"firstLogin":null,
                        "finishedJobs":null,"finishedBids":null,"stars":null,"profession":null,
                        "highlight":null,"ratings":null,"topSkills":[]}],
                        "pageable":{"sort":{"empty":true,"sorted":false,"unsorted":true},
                        "offset":0,"pageNumber":0,"pageSize":1,"paged":true,"unpaged":false},
                        "totalElements":1,"totalPages":1,"last":true,"size":1,"number":0,
                        "sort":{"empty":true,"sorted":false,"unsorted":true},"numberOfElements":1,"first":true,"empty":false}
                        """);
    }
}
