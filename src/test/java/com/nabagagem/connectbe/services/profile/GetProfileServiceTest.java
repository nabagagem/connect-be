package com.nabagagem.connectbe.services.profile;

import com.nabagagem.connectbe.domain.profile.AvailabilityType;
import com.nabagagem.connectbe.domain.profile.ProfileMetrics;
import com.nabagagem.connectbe.domain.profile.ProfilePayload;
import com.nabagagem.connectbe.domain.profile.SkillPayload;
import com.nabagagem.connectbe.domain.profile.SkillReadPayload;
import com.nabagagem.connectbe.entities.CertificationPayload;
import com.nabagagem.connectbe.entities.ConnectProfile;
import com.nabagagem.connectbe.entities.LinkType;
import com.nabagagem.connectbe.entities.PersonalInfo;
import com.nabagagem.connectbe.entities.ProfileBio;
import com.nabagagem.connectbe.entities.ProfileSkill;
import com.nabagagem.connectbe.entities.ProfileType;
import com.nabagagem.connectbe.repos.ProfileRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetProfileServiceTest {

    @Mock
    private ProfileService mockProfileService;
    @Mock
    private ProfilePayloadMapper mockProfilePayloadMapper;
    @Mock
    private ProfileRepo mockProfileRepo;

    private GetProfileService getProfileServiceUnderTest;

    @BeforeEach
    void setUp() throws Exception {
        getProfileServiceUnderTest = new GetProfileService(mockProfileService, mockProfilePayloadMapper,
                mockProfileRepo);
    }

    @Test
    void testGetProfile() {
        // Setup
        UUID profileId = UUID.fromString("40d844a5-0105-4fce-a04d-bbd86fa8191d");
        final ProfilePayload expectedResult = new ProfilePayload(
                profileId,
                null, PersonalInfo.builder().build(),
                Set.of(new SkillReadPayload(UUID.fromString("608f980f-126c-4448-b715-5eefb2919d62"),
                        new SkillPayload("name", 0, ProfileSkill.SkillLevel.ONE_2_THREE, false))),
                Set.of(new CertificationPayload("title", 2020)), ProfileBio.builder().build(),
                Map.ofEntries(Map.entry(DayOfWeek.FRIDAY, AvailabilityType.MORNING)),
                Map.ofEntries(Map.entry(LinkType.FACEBOOK, "value")),
                new ProfileMetrics(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneId.of("UTC")),
                        ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneId.of("UTC"))), ProfileType.USER);
        ConnectProfile profile = ConnectProfile.builder()
                .id(profileId)
                .build();
        when(mockProfileService.findOrCreate(profileId))
                .thenReturn(profile);

        // Configure ProfileRepo.findForProfileRead(...).
        final Optional<ConnectProfile> connectProfile = Optional.of(profile);
        when(mockProfileRepo.findForProfileRead(profileId))
                .thenReturn(connectProfile);

        // Configure ProfilePayloadMapper.toPayload(...).
        final ProfilePayload profilePayload = new ProfilePayload(
                profileId,
                null, PersonalInfo.builder().build(),
                Set.of(new SkillReadPayload(UUID.fromString("53a4f183-ffbb-4e02-901b-8a2b2b2c4227"),
                        new SkillPayload("name", 0, ProfileSkill.SkillLevel.ONE_2_THREE, false))),
                Set.of(new CertificationPayload("title", 2020)), ProfileBio.builder().build(),
                Map.ofEntries(Map.entry(DayOfWeek.FRIDAY, AvailabilityType.MORNING)),
                Map.ofEntries(Map.entry(LinkType.FACEBOOK, "value")),
                new ProfileMetrics(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneId.of("UTC")),
                        ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneId.of("UTC"))), ProfileType.USER);
        when(mockProfilePayloadMapper.toPayload(profile)).thenReturn(profilePayload);

        // Run the test
        final ProfilePayload result = getProfileServiceUnderTest.getProfile(
                profileId);

        // Verify the results
        assertThatJson(result).isEqualTo("""
                {"id":"40d844a5-0105-4fce-a04d-bbd86fa8191d","parentId":null,"personalInfo":{"publicName":null,"slug":null,"profession":null,
                "highlightTitle":null,"profileCategory":null,"otherCategory":null,"workingMode":null,"city":null,"publicProfile":null,"available":null,
                "tags":null,"amountPerHour":null,"email":null,"enableMessageEmail":null,"ready":false,"language":"PORTUGUESE"},
                "skills":[{"id":"53a4f183-ffbb-4e02-901b-8a2b2b2c4227","name":"name","certifications":0,"level":"ONE_2_THREE","top":false}],
                "certifications":[{"title":"title","year":2020}],"bio":{"description":null,"professionalRecord":null},"availabilities":{"FRIDAY":"MORNING"},
                "links":{"FACEBOOK":"value"},"profileMetrics":{"lastActivity":1577836800.000000000,"firstLogin":1577836800.000000000},"profileType":"USER"}
                """);
    }

    @Test
    void testGetProfile_ProfileRepoReturnsAbsent() {
        // Setup
        when(mockProfileService.findOrCreate(UUID.fromString("40d844a5-0105-4fce-a04d-bbd86fa8191d")))
                .thenReturn(ConnectProfile.builder().build());
        when(mockProfileRepo.findForProfileRead(UUID.fromString("40d844a5-0105-4fce-a04d-bbd86fa8191d")))
                .thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(() -> getProfileServiceUnderTest.getProfile(
                UUID.fromString("40d844a5-0105-4fce-a04d-bbd86fa8191d"))).isInstanceOf(NoSuchElementException.class);
    }
}
