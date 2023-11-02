package com.nabagagem.connectbe.services.profile;

import com.nabagagem.connectbe.domain.profile.AvailabilityType;
import com.nabagagem.connectbe.domain.profile.SkillPayload;
import com.nabagagem.connectbe.domain.profile.SkillReadPayload;
import com.nabagagem.connectbe.entities.Availability;
import com.nabagagem.connectbe.entities.Certification;
import com.nabagagem.connectbe.entities.CertificationPayload;
import com.nabagagem.connectbe.entities.ConnectProfile;
import com.nabagagem.connectbe.entities.LinkType;
import com.nabagagem.connectbe.entities.ProfileLink;
import com.nabagagem.connectbe.entities.ProfileSkill;
import com.nabagagem.connectbe.entities.Skill;
import com.nabagagem.connectbe.repos.SkillRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.OptimisticLockingFailureException;

import java.time.DayOfWeek;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProfileMapperTest {

    @Mock
    private SkillRepo mockSkillRepo;

    private ProfileMapper profileMapperUnderTest;

    @BeforeEach
    void setUp() throws Exception {
        profileMapperUnderTest = new ProfileMapper(mockSkillRepo);
    }

    @Test
    void testToProfileSkill() {
        // Setup
        final SkillPayload skill = new SkillPayload("name", 0, ProfileSkill.SkillLevel.ONE_2_THREE, false);
        final ConnectProfile profile = ConnectProfile.builder().build();
        final ProfileSkill expectedResult = ProfileSkill.builder()
                .id(UUID.fromString("6d4695af-1c23-4fa9-b459-001df21bf3d3"))
                .skill(Skill.builder()
                        .name("name")
                        .build())
                .certifications(0)
                .level(ProfileSkill.SkillLevel.ONE_2_THREE)
                .top(false)
                .profile(ConnectProfile.builder().build())
                .build();

        // Configure SkillRepo.findByName(...).
        final Optional<Skill> skill1 = Optional.of(Skill.builder()
                .name("name")
                .build());
        when(mockSkillRepo.findByName("name")).thenReturn(skill1);

        // Run the test
        final ProfileSkill result = profileMapperUnderTest.toProfileSkill(skill, profile);

        // Verify the results
        assertThat(result)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expectedResult);
    }

    @Test
    void testToProfileSkill_SkillRepoFindByNameReturnsAbsent() {
        // Setup
        final SkillPayload skill = new SkillPayload("name", 0, ProfileSkill.SkillLevel.ONE_2_THREE, false);
        final ConnectProfile profile = ConnectProfile.builder().build();
        final ProfileSkill expectedResult = ProfileSkill.builder()
                .id(UUID.fromString("6d4695af-1c23-4fa9-b459-001df21bf3d3"))
                .skill(Skill.builder()
                        .name("name")
                        .build())
                .certifications(0)
                .level(ProfileSkill.SkillLevel.ONE_2_THREE)
                .top(false)
                .profile(ConnectProfile.builder().build())
                .build();
        when(mockSkillRepo.findByName("name")).thenReturn(Optional.empty());
        when(mockSkillRepo.save(Skill.builder()
                .name("name")
                .build())).thenReturn(Skill.builder()
                .name("name")
                .build());

        // Run the test
        final ProfileSkill result = profileMapperUnderTest.toProfileSkill(skill, profile);

        // Verify the results
        assertThat(result)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expectedResult);
    }

    @Test
    void testToProfileSkill_SkillRepoSaveThrowsOptimisticLockingFailureException() {
        // Setup
        final SkillPayload skill = new SkillPayload("name", 0, ProfileSkill.SkillLevel.ONE_2_THREE, false);
        final ConnectProfile profile = ConnectProfile.builder().build();
        when(mockSkillRepo.findByName("name")).thenReturn(Optional.empty());
        when(mockSkillRepo.save(Skill.builder()
                .name("name")
                .build())).thenThrow(OptimisticLockingFailureException.class);

        // Run the test
        assertThatThrownBy(() -> profileMapperUnderTest.toProfileSkill(skill, profile))
                .isInstanceOf(OptimisticLockingFailureException.class);
    }

    @Test
    void testToSkillPayload() {
        // Setup
        final ProfileSkill profileSkill = ProfileSkill.builder()
                .id(UUID.fromString("6d4695af-1c23-4fa9-b459-001df21bf3d3"))
                .skill(Skill.builder()
                        .name("name")
                        .build())
                .certifications(0)
                .level(ProfileSkill.SkillLevel.ONE_2_THREE)
                .top(false)
                .profile(ConnectProfile.builder().build())
                .build();
        final SkillPayload expectedResult = new SkillPayload("name", 0, ProfileSkill.SkillLevel.ONE_2_THREE, false);

        // Run the test
        final SkillPayload result = profileMapperUnderTest.toSkillPayload(profileSkill);

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testToCertification() {
        // Setup
        final CertificationPayload certificationPayload = new CertificationPayload("title", 2020);
        final ConnectProfile profile = ConnectProfile.builder()
                .id(UUID.randomUUID())
                .build();
        final Certification expectedResult = Certification.builder()
                .profile(profile)
                .title("title")
                .year(2020)
                .build();

        // Run the test
        final Certification result = profileMapperUnderTest.toCertification(certificationPayload, profile);

        // Verify the results
        assertThat(result)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expectedResult);
    }

    @Test
    void testToCertPayload() {
        // Setup
        final Certification certification = Certification.builder()
                .id(UUID.fromString("74713ce3-83b0-44fe-a8b3-895fd858ab7a"))
                .profile(ConnectProfile.builder().build())
                .title("title")
                .year(2020)
                .build();
        final CertificationPayload expectedResult = new CertificationPayload("title", 2020);

        // Run the test
        final CertificationPayload result = profileMapperUnderTest.toCertPayload(certification);

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testToCertsPayload() {
        // Setup
        final Set<Certification> certifications = Set.of(Certification.builder()
                .id(UUID.fromString("74713ce3-83b0-44fe-a8b3-895fd858ab7a"))
                .profile(ConnectProfile.builder().build())
                .title("title")
                .year(2020)
                .build());
        final Set<CertificationPayload> expectedResult = Set.of(new CertificationPayload("title", 2020));

        // Run the test
        final Set<CertificationPayload> result = profileMapperUnderTest.toCertsPayload(certifications);

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testMapAvailabilities() {
        // Setup
        final Map<DayOfWeek, AvailabilityType> availabilities = Map.ofEntries(
                Map.entry(DayOfWeek.FRIDAY, AvailabilityType.MORNING));
        final ConnectProfile profile = ConnectProfile.builder().build();
        final Set<Availability> expectedResult = Set.of(Availability.builder()
                .id(UUID.fromString("1dde30a8-7b51-4562-8b93-e1d994077815"))
                .profile(ConnectProfile.builder().build())
                .dayOfWeek(DayOfWeek.FRIDAY)
                .availabilityType(AvailabilityType.MORNING)
                .build());

        // Run the test
        final Set<Availability> result = profileMapperUnderTest.mapAvailabilities(availabilities, profile);

        // Verify the results
        assertThat(result)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expectedResult);
    }

    @Test
    void testToAvailPayload() {
        // Setup
        final Set<Availability> availabilities = Set.of(Availability.builder()
                .id(UUID.fromString("1dde30a8-7b51-4562-8b93-e1d994077815"))
                .profile(ConnectProfile.builder().build())
                .dayOfWeek(DayOfWeek.FRIDAY)
                .availabilityType(AvailabilityType.MORNING)
                .build());
        final Map<DayOfWeek, AvailabilityType> expectedResult = Map.ofEntries(
                Map.entry(DayOfWeek.FRIDAY, AvailabilityType.MORNING));

        // Run the test
        final Map<DayOfWeek, AvailabilityType> result = profileMapperUnderTest.toAvailPayload(availabilities);

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testToSkillReadPayload() {
        // Setup
        final ProfileSkill profileSkill = ProfileSkill.builder()
                .id(UUID.fromString("6d4695af-1c23-4fa9-b459-001df21bf3d3"))
                .skill(Skill.builder()
                        .name("name")
                        .build())
                .certifications(0)
                .level(ProfileSkill.SkillLevel.ONE_2_THREE)
                .top(false)
                .profile(ConnectProfile.builder().build())
                .build();
        final SkillReadPayload expectedResult = new SkillReadPayload(
                UUID.fromString("b95399c3-27b0-4261-a969-52b90cc68b41"),
                new SkillPayload("name", 0, ProfileSkill.SkillLevel.ONE_2_THREE, false));

        // Run the test
        final SkillReadPayload result = profileMapperUnderTest.toSkillReadPayload(profileSkill);

        // Verify the results
        assertThat(result)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expectedResult);
    }

    @Test
    void testToLinksMap() {
        // Setup
        final Set<ProfileLink> profileLinks = Set.of(ProfileLink.builder()
                .linkType(LinkType.FACEBOOK)
                .linkURL("value")
                .build());
        final Map<LinkType, String> expectedResult = Map.ofEntries(Map.entry(LinkType.FACEBOOK, "value"));

        // Run the test
        final Map<LinkType, String> result = profileMapperUnderTest.toLinksMap(profileLinks);

        // Verify the results
        assertThat(result)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expectedResult);
    }
}
