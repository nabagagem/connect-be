package com.nabagagem.connectbe.services.profile;

import com.nabagagem.connectbe.domain.profile.AvailabilityType;
import com.nabagagem.connectbe.domain.profile.ProfilePayload;
import com.nabagagem.connectbe.domain.profile.SkillPayload;
import com.nabagagem.connectbe.domain.profile.SkillReadPayload;
import com.nabagagem.connectbe.entities.Audit;
import com.nabagagem.connectbe.entities.Availability;
import com.nabagagem.connectbe.entities.Certification;
import com.nabagagem.connectbe.entities.CertificationPayload;
import com.nabagagem.connectbe.entities.ConnectProfile;
import com.nabagagem.connectbe.entities.LinkType;
import com.nabagagem.connectbe.entities.PersonalInfo;
import com.nabagagem.connectbe.entities.ProfileBio;
import com.nabagagem.connectbe.entities.ProfileLink;
import com.nabagagem.connectbe.entities.ProfileSkill;
import com.nabagagem.connectbe.entities.ProfileType;
import com.nabagagem.connectbe.entities.Skill;
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

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProfilePayloadMapperTest {

    @Mock
    private ProfileMapper mockProfileMapper;

    private ProfilePayloadMapper profilePayloadMapperUnderTest;

    @BeforeEach
    void setUp() throws Exception {
        profilePayloadMapperUnderTest = new ProfilePayloadMapper(mockProfileMapper);
    }

    @Test
    void testToPayload() {
        // Setup
        ProfileLink profileLink = ProfileLink.builder()
                .linkURL("linkURL")
                .linkType(LinkType.X)
                .build();
        PersonalInfo personalInfo = PersonalInfo.builder()
                .city("city")
                .build();
        final ConnectProfile profile = ConnectProfile.builder()
                .id(UUID.fromString("1c849bae-1060-4244-a134-93ea3702da79"))
                .personalInfo(personalInfo)
                .profileSkills(Set.of(ProfileSkill.builder()
                        .skill(Skill.builder()
                                .name("name")
                                .build())
                        .level(ProfileSkill.SkillLevel.ONE_2_THREE)
                        .top(false)
                        .build()))
                .certifications(Set.of(Certification.builder().build()))
                .availabilities(Set.of(Availability.builder().build()))
                .profileBio(ProfileBio.builder().build())
                .lastActivity(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneId.of("UTC")))
                .profileType(ProfileType.USER)
                .audit(new Audit())
                .profileLinks(Set.of(profileLink))
                .build();

        // Configure ProfileMapper.toSkillReadPayload(...).
        final SkillReadPayload skillReadPayload = new SkillReadPayload(
                UUID.fromString("856e8713-e6b1-4cca-a5d5-6a9743386710"),
                new SkillPayload("name", 0, ProfileSkill.SkillLevel.ONE_2_THREE, false));
        when(mockProfileMapper.toSkillReadPayload(ProfileSkill.builder()
                .skill(Skill.builder()
                        .name("name")
                        .build())
                .level(ProfileSkill.SkillLevel.ONE_2_THREE)
                .top(false)
                .build())).thenReturn(skillReadPayload);

        // Configure ProfileMapper.toCertsPayload(...).
        final Set<CertificationPayload> certificationPayloads = Set.of(new CertificationPayload("title", 2020));
        when(mockProfileMapper.toCertsPayload(Set.of(Certification.builder().build())))
                .thenReturn(certificationPayloads);

        // Configure ProfileMapper.toAvailPayload(...).
        final Map<DayOfWeek, AvailabilityType> dayOfWeekAvailabilityTypeMap = Map.ofEntries(
                Map.entry(DayOfWeek.FRIDAY, AvailabilityType.MORNING));
        when(mockProfileMapper.toAvailPayload(Set.of(Availability.builder().build())))
                .thenReturn(dayOfWeekAvailabilityTypeMap);

        // Configure ProfileMapper.toLinksMap(...).
        final Map<LinkType, String> linkTypeStringMap = Map.ofEntries(Map.entry(LinkType.FACEBOOK, "value"));
        when(mockProfileMapper.toLinksMap(Set.of(profileLink))).thenReturn(linkTypeStringMap);

        // Run the test
        final ProfilePayload result = profilePayloadMapperUnderTest.toPayload(profile);

        // Verify the results
        assertThatJson(result).isEqualTo("""
                {"id":"1c849bae-1060-4244-a134-93ea3702da79","parentId":null,
                "personalInfo":{"publicName":null,"slug":null,"profession":null,"highlightTitle":null,
                "profileCategory":null,"otherCategory":null,"workingMode":null,"city":"city","publicProfile":null,
                "available":null,"tags":null,"amountPerHour":null,"email":null,"enableMessageEmail":null,"ready":false,
                "language":"PORTUGUESE"},"skills":[{"id":"856e8713-e6b1-4cca-a5d5-6a9743386710","name":"name",
                "certifications":0,"level":"ONE_2_THREE","top":false}],"certifications":[{"title":"title","year":2020}],
                "bio":{"description":null,"professionalRecord":null},"availabilities":{"FRIDAY":"MORNING"},
                "links":{"FACEBOOK":"value"},"profileMetrics":{"lastActivity":1577836800.000000000,"firstLogin":null},"profileType":"USER"}
                """);
    }
}
