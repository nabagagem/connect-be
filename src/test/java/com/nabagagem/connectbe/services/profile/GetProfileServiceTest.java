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
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetProfileServiceTest {

    @Mock
    private ProfileService mockProfileService;
    @Mock
    private ProfilePayloadMapper mockProfilePayloadMapper;

    private GetProfileService getProfileServiceUnderTest;

    @BeforeEach
    void setUp() throws Exception {
        getProfileServiceUnderTest = new GetProfileService(mockProfileService, mockProfilePayloadMapper);
    }

    @Test
    void testGetProfile() {
        // Setup
        final ProfilePayload expectedResult = new ProfilePayload(
                UUID.fromString("a6a2eb16-02be-4734-b23f-db8046700be4"),
                UUID.fromString("79d66a35-2fb4-454d-99e6-46af0153b0c7"), PersonalInfo.builder().build(),
                Set.of(new SkillReadPayload(UUID.fromString("d8087bf5-ae95-488d-9b90-1d9cfa67c96f"),
                        new SkillPayload("name", 0, ProfileSkill.SkillLevel.ONE_2_THREE, false))),
                Set.of(new CertificationPayload("title", 2020)), ProfileBio.builder().build(),
                Map.ofEntries(Map.entry(DayOfWeek.FRIDAY, AvailabilityType.MORNING)),
                Map.ofEntries(Map.entry(LinkType.FACEBOOK, "value")),
                new ProfileMetrics(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneId.of("UTC")),
                        ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneId.of("UTC"))), ProfileType.USER);
        ConnectProfile profile = ConnectProfile.builder()
                .id(UUID.randomUUID())
                .build();
        when(mockProfileService.findOrCreate(UUID.fromString("54792cf5-a901-4d54-b62d-ee622bc44f98")))
                .thenReturn(profile);

        // Configure ProfilePayloadMapper.toPayload(...).
        when(mockProfilePayloadMapper.toPayload(profile)).thenReturn(expectedResult);

        // Run the test
        final ProfilePayload result = getProfileServiceUnderTest.getProfile(
                UUID.fromString("54792cf5-a901-4d54-b62d-ee622bc44f98"));

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }
}
