package com.nabagagem.connectbe.services.profile;

import com.nabagagem.connectbe.domain.job.JobCategory;
import com.nabagagem.connectbe.domain.profile.AvailabilityType;
import com.nabagagem.connectbe.domain.profile.ProfileMetrics;
import com.nabagagem.connectbe.domain.profile.ProfilePayload;
import com.nabagagem.connectbe.domain.profile.ProfileSearchItemPayload;
import com.nabagagem.connectbe.domain.profile.ProfileSearchParams;
import com.nabagagem.connectbe.domain.profile.SkillPayload;
import com.nabagagem.connectbe.domain.profile.SkillReadPayload;
import com.nabagagem.connectbe.domain.profile.TopSkillPayload;
import com.nabagagem.connectbe.domain.profile.WorkingMode;
import com.nabagagem.connectbe.entities.CertificationPayload;
import com.nabagagem.connectbe.entities.ConnectProfile;
import com.nabagagem.connectbe.entities.LinkType;
import com.nabagagem.connectbe.entities.PersonalInfo;
import com.nabagagem.connectbe.entities.ProfileBio;
import com.nabagagem.connectbe.entities.ProfileSkill;
import com.nabagagem.connectbe.entities.ProfileType;
import com.nabagagem.connectbe.repos.ProfileRepo;
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

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProfileSearchServiceTest {

    @Mock
    private ProfileRepo mockProfileRepo;
    @Mock
    private KeywordService mockKeywordService;
    @Mock
    private ProfilePayloadMapper mockProfileMapper;

    private ProfileSearchService profileSearchServiceUnderTest;

    @BeforeEach
    void setUp() throws Exception {
        profileSearchServiceUnderTest = new ProfileSearchService(mockProfileRepo, mockKeywordService,
                mockProfileMapper);
    }

    @Test
    void testSearchFor() {
        // Setup
        final ProfileSearchParams profileSearchParams = new ProfileSearchParams(Set.of(JobCategory.GASTRONOMY),
                Set.of(WorkingMode.REMOTE), "text");
        when(mockKeywordService.extractFrom("text")).thenReturn(Set.of("value"));

        // Configure ProfileRepo.searchIdsFor(...).
        UUID profileId = UUID.fromString("a3c5241f-98ba-4951-a4c7-5225a9b88c0f");
        final Page<UUID> uuids = new PageImpl<>(List.of(profileId));
        when(mockProfileRepo.searchIdsFor(eq(Set.of(WorkingMode.REMOTE)), eq(Set.of(JobCategory.GASTRONOMY)),
                eq(Set.of("value")), eq(false), any(Pageable.class))).thenReturn(uuids);

        // Configure ProfileRepo.searchProfilesBy(...).
        ConnectProfile profile = ConnectProfile.builder()
                .id(UUID.randomUUID())
                .build();
        final List<ConnectProfile> connectProfiles = List.of(profile);
        when(mockProfileRepo.searchProfilesBy(
                List.of(profileId))).thenReturn(connectProfiles);

        // Configure ProfilePayloadMapper.toItemPayload(...).
        final ProfileSearchItemPayload profileSearchItemPayload = new ProfileSearchItemPayload(
                new ProfilePayload(UUID.fromString("32e8521e-6074-4411-bc40-b3e72df149c1"),
                        UUID.fromString("1649307d-6999-4b56-96d2-7706804ea62b"), PersonalInfo.builder().build(),
                        Set.of(new SkillReadPayload(UUID.fromString("2e470087-2484-4228-975f-e03c168f4b87"),
                                new SkillPayload("name", 0, ProfileSkill.SkillLevel.ONE_2_THREE, false))),
                        Set.of(new CertificationPayload("title", 2020)), ProfileBio.builder().build(),
                        Map.ofEntries(Map.entry(DayOfWeek.FRIDAY, AvailabilityType.MORNING)),
                        Map.ofEntries(Map.entry(LinkType.FACEBOOK, "value")),
                        new ProfileMetrics(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneId.of("UTC")),
                                ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneId.of("UTC"))),
                        ProfileType.USER), Set.of(new TopSkillPayload("level", "name")));
        when(mockProfileMapper.toItemPayload(profile)).thenReturn(profileSearchItemPayload);

        // Run the test
        final Page<ProfileSearchItemPayload> result = profileSearchServiceUnderTest.searchFor(profileSearchParams,
                PageRequest.of(0, 1));

        // Verify the results
    }
}
