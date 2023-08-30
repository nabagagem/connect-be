package com.nabagagem.connectbe.services.profile;

import com.nabagagem.connectbe.domain.exceptions.BadRequestException;
import com.nabagagem.connectbe.domain.profile.AvailabilityCommand;
import com.nabagagem.connectbe.domain.profile.AvailabilityType;
import com.nabagagem.connectbe.domain.profile.BioCommand;
import com.nabagagem.connectbe.domain.profile.CertificationsCommand;
import com.nabagagem.connectbe.domain.profile.PatchSkillCommand;
import com.nabagagem.connectbe.domain.profile.PatchSkillPayload;
import com.nabagagem.connectbe.domain.profile.PersonalInfoCommand;
import com.nabagagem.connectbe.domain.profile.ProfileMetrics;
import com.nabagagem.connectbe.domain.profile.ProfilePayload;
import com.nabagagem.connectbe.domain.profile.SkillCommand;
import com.nabagagem.connectbe.domain.profile.SkillPayload;
import com.nabagagem.connectbe.domain.profile.SkillReadPayload;
import com.nabagagem.connectbe.domain.rating.ProfileRatingPayload;
import com.nabagagem.connectbe.entities.Availability;
import com.nabagagem.connectbe.entities.Certification;
import com.nabagagem.connectbe.entities.CertificationPayload;
import com.nabagagem.connectbe.entities.ConnectProfile;
import com.nabagagem.connectbe.entities.PersonalInfo;
import com.nabagagem.connectbe.entities.ProfileBio;
import com.nabagagem.connectbe.entities.ProfileSkill;
import com.nabagagem.connectbe.entities.ProfileType;
import com.nabagagem.connectbe.repos.AvailabilityRepo;
import com.nabagagem.connectbe.repos.CertificationRepo;
import com.nabagagem.connectbe.repos.ProfileRepo;
import com.nabagagem.connectbe.repos.ProfileSkillRepo;
import com.nabagagem.connectbe.services.rating.RatingListService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.net.URL;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {

    @Mock
    private ProfileRepo mockProfileRepo;
    @Mock
    private ProfileSkillRepo mockProfileSkillRepo;
    @Mock
    private CertificationRepo mockCertificationRepo;
    @Mock
    private ProfileMapper mockProfileMapper;
    @Mock
    private AvailabilityRepo mockAvailabilityRepo;
    @Mock
    private ProfileInitService mockProfileInitService;
    @Mock
    private ProfileMetricsService mockProfileMetricsService;
    @Mock
    private RatingListService mockRatingListService;
    @Mock
    private ProfileIndexingService mockProfileIndexingService;

    private ProfileService profileServiceUnderTest;

    @BeforeEach
    void setUp() {
        profileServiceUnderTest = new ProfileService(mockProfileRepo, mockProfileSkillRepo, mockCertificationRepo,
                mockProfileMapper, mockAvailabilityRepo, mockProfileInitService, mockProfileMetricsService,
                mockRatingListService, mockProfileIndexingService);
    }

    @Test
    void testUpdateInfo() {
        // Setup
        ArgumentCaptor<ConnectProfile> profileArgumentCaptor = ArgumentCaptor.forClass(ConnectProfile.class);
        UUID profileId = UUID.fromString("f64c89e4-75a8-48a5-aaee-59a641bdd110");
        final PersonalInfoCommand personalInfoCommand = new PersonalInfoCommand(
                profileId, PersonalInfo.builder()
                .slug("new-slug")
                .build());
        final ConnectProfile expectedResult = ConnectProfile.builder()
                .id(profileId)
                .personalInfo(PersonalInfo.builder()
                        .slug("new-slug")
                        .build())
                .profileSkills(Set.of(ProfileSkill.builder()
                        .id(UUID.fromString("52c26f01-86bd-4eab-97ee-d7d28b4c036f"))
                        .top(false)
                        .build()))
                .certifications(Set.of(Certification.builder().build()))
                .availabilities(Set.of(Availability.builder().build()))
                .profileBio(ProfileBio.builder().build())
                .keywords(Set.of("value"))
                .build();

        // Configure ProfileRepo.findById(...).
        final Optional<ConnectProfile> connectProfile = Optional.of(ConnectProfile.builder()
                .id(profileId)
                .personalInfo(PersonalInfo.builder()
                        .slug("slug")
                        .build())
                .profileSkills(Set.of(ProfileSkill.builder()
                        .id(UUID.fromString("52c26f01-86bd-4eab-97ee-d7d28b4c036f"))
                        .top(false)
                        .build()))
                .certifications(Set.of(Certification.builder().build()))
                .availabilities(Set.of(Availability.builder().build()))
                .profileBio(ProfileBio.builder().build())
                .keywords(Set.of("value"))
                .build());
        when(mockProfileRepo.findById(profileId))
                .thenReturn(connectProfile);

        when(mockProfileIndexingService.extractFrom(ConnectProfile.builder()
                .id(profileId)
                .personalInfo(PersonalInfo.builder()
                        .slug("slug")
                        .build())
                .profileSkills(Set.of(ProfileSkill.builder()
                        .id(UUID.fromString("52c26f01-86bd-4eab-97ee-d7d28b4c036f"))
                        .top(false)
                        .build()))
                .certifications(Set.of(Certification.builder().build()))
                .availabilities(Set.of(Availability.builder().build()))
                .profileBio(ProfileBio.builder().build())
                .keywords(Set.of("value"))
                .build())).thenReturn(Set.of("value"));

        // Configure ProfileRepo.save(...).
        when(mockProfileRepo.save(any())).thenReturn(expectedResult);

        // Run the test
        final ConnectProfile result = profileServiceUnderTest.updateInfo(personalInfoCommand);

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
        verify(mockProfileRepo).save(profileArgumentCaptor.capture());
        assertThat(profileArgumentCaptor.getValue())
                .usingRecursiveComparison()
                .isEqualTo(expectedResult);
    }

    @Test
    void testUpdateInfo_ProfileRepoFindByIdReturnsAbsent() {
        // Setup
        UUID profileId = UUID.fromString("f64c89e4-75a8-48a5-aaee-59a641bdd110");
        final PersonalInfoCommand personalInfoCommand = new PersonalInfoCommand(
                profileId, PersonalInfo.builder()
                .slug("slug")
                .build());
        final ConnectProfile expectedResult = ConnectProfile.builder()
                .id(profileId)
                .personalInfo(PersonalInfo.builder()
                        .slug("slug")
                        .build())
                .profileSkills(Set.of(ProfileSkill.builder()
                        .id(UUID.fromString("52c26f01-86bd-4eab-97ee-d7d28b4c036f"))
                        .top(false)
                        .build()))
                .certifications(Set.of(Certification.builder().build()))
                .availabilities(Set.of(Availability.builder().build()))
                .profileBio(ProfileBio.builder().build())
                .keywords(Set.of("value"))
                .build();
        when(mockProfileRepo.findById(profileId))
                .thenReturn(Optional.empty());

        // Configure ProfileRepo.save(...).
        final ConnectProfile profile = ConnectProfile.builder()
                .id(profileId)
                .personalInfo(PersonalInfo.builder()
                        .slug("slug")
                        .build())
                .profileSkills(Set.of(ProfileSkill.builder()
                        .id(UUID.fromString("52c26f01-86bd-4eab-97ee-d7d28b4c036f"))
                        .top(false)
                        .build()))
                .certifications(Set.of(Certification.builder().build()))
                .availabilities(Set.of(Availability.builder().build()))
                .profileBio(ProfileBio.builder().build())
                .keywords(Set.of("value"))
                .build();
        when(mockProfileRepo.save(ConnectProfile.builder()
                .id(profileId)
                .personalInfo(PersonalInfo.builder()
                        .slug("slug")
                        .build())
                .profileSkills(Set.of(ProfileSkill.builder()
                        .id(UUID.fromString("52c26f01-86bd-4eab-97ee-d7d28b4c036f"))
                        .top(false)
                        .build()))
                .certifications(Set.of(Certification.builder().build()))
                .availabilities(Set.of(Availability.builder().build()))
                .profileBio(ProfileBio.builder().build())
                .keywords(Set.of("value"))
                .build())).thenReturn(profile);

        // Run the test
        final ConnectProfile result = profileServiceUnderTest.updateInfo(personalInfoCommand);

        // Verify the results
        assertThat(result)
                .usingRecursiveComparison()
                .isEqualTo(expectedResult);
    }

    @Test
    void testFindOrInit() {
        // Setup
        final ConnectProfile expectedResult = ConnectProfile.builder()
                .id(UUID.fromString("8d2b6d88-079b-40a0-9751-bfe2d2e6fa1b"))
                .personalInfo(PersonalInfo.builder()
                        .slug("slug")
                        .build())
                .profileSkills(Set.of(ProfileSkill.builder()
                        .id(UUID.fromString("52c26f01-86bd-4eab-97ee-d7d28b4c036f"))
                        .top(false)
                        .build()))
                .certifications(Set.of(Certification.builder().build()))
                .availabilities(Set.of(Availability.builder().build()))
                .profileBio(ProfileBio.builder().build())
                .keywords(Set.of("value"))
                .build();

        // Configure ProfileRepo.findById(...).
        final Optional<ConnectProfile> connectProfile = Optional.of(ConnectProfile.builder()
                .id(UUID.fromString("8d2b6d88-079b-40a0-9751-bfe2d2e6fa1b"))
                .personalInfo(PersonalInfo.builder()
                        .slug("slug")
                        .build())
                .profileSkills(Set.of(ProfileSkill.builder()
                        .id(UUID.fromString("52c26f01-86bd-4eab-97ee-d7d28b4c036f"))
                        .top(false)
                        .build()))
                .certifications(Set.of(Certification.builder().build()))
                .availabilities(Set.of(Availability.builder().build()))
                .profileBio(ProfileBio.builder().build())
                .keywords(Set.of("value"))
                .build());
        when(mockProfileRepo.findById(UUID.fromString("8d2b6d88-079b-40a0-9751-bfe2d2e6fa1b")))
                .thenReturn(connectProfile);

        // Run the test
        final ConnectProfile result = profileServiceUnderTest.findOrInit(
                UUID.fromString("8d2b6d88-079b-40a0-9751-bfe2d2e6fa1b"));

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testFindOrInit_ProfileRepoReturnsAbsent() {
        // Setup
        final ConnectProfile expectedResult = ConnectProfile.builder()
                .id(UUID.fromString("8d2b6d88-079b-40a0-9751-bfe2d2e6fa1b"))
                .personalInfo(PersonalInfo.builder()
                        .slug("slug")
                        .build())
                .profileSkills(Set.of(ProfileSkill.builder()
                        .id(UUID.fromString("52c26f01-86bd-4eab-97ee-d7d28b4c036f"))
                        .top(false)
                        .build()))
                .certifications(Set.of(Certification.builder().build()))
                .availabilities(Set.of(Availability.builder().build()))
                .profileBio(ProfileBio.builder().build())
                .keywords(Set.of("value"))
                .build();
        when(mockProfileRepo.findById(UUID.fromString("8d2b6d88-079b-40a0-9751-bfe2d2e6fa1b")))
                .thenReturn(Optional.empty());

        // Run the test
        final ConnectProfile result = profileServiceUnderTest.findOrInit(
                UUID.fromString("8d2b6d88-079b-40a0-9751-bfe2d2e6fa1b"));

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testGetInfo() {
        // Setup
        final PersonalInfo expectedResult = PersonalInfo.builder()
                .slug("slug")
                .build();

        // Configure ProfileRepo.findById(...).
        final Optional<ConnectProfile> connectProfile = Optional.of(ConnectProfile.builder()
                .id(UUID.fromString("8d2b6d88-079b-40a0-9751-bfe2d2e6fa1b"))
                .personalInfo(PersonalInfo.builder()
                        .slug("slug")
                        .build())
                .profileSkills(Set.of(ProfileSkill.builder()
                        .id(UUID.fromString("52c26f01-86bd-4eab-97ee-d7d28b4c036f"))
                        .top(false)
                        .build()))
                .certifications(Set.of(Certification.builder().build()))
                .availabilities(Set.of(Availability.builder().build()))
                .profileBio(ProfileBio.builder().build())
                .keywords(Set.of("value"))
                .build());
        when(mockProfileRepo.findById(UUID.fromString("70a33fee-d415-4324-a1de-a3117d5aa691")))
                .thenReturn(connectProfile);

        // Run the test
        final PersonalInfo result = profileServiceUnderTest.getInfo(
                UUID.fromString("70a33fee-d415-4324-a1de-a3117d5aa691"));

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testGetInfo_ProfileRepoReturnsAbsent() {
        // Setup
        final PersonalInfo expectedResult = PersonalInfo.builder()
                .slug("slug")
                .build();
        when(mockProfileRepo.findById(UUID.fromString("70a33fee-d415-4324-a1de-a3117d5aa691")))
                .thenReturn(Optional.empty());

        // Configure ProfileInitService.initFromAuth(...).
        final ConnectProfile profile = ConnectProfile.builder()
                .id(UUID.fromString("8d2b6d88-079b-40a0-9751-bfe2d2e6fa1b"))
                .personalInfo(PersonalInfo.builder()
                        .slug("slug")
                        .build())
                .profileSkills(Set.of(ProfileSkill.builder()
                        .id(UUID.fromString("52c26f01-86bd-4eab-97ee-d7d28b4c036f"))
                        .top(false)
                        .build()))
                .certifications(Set.of(Certification.builder().build()))
                .availabilities(Set.of(Availability.builder().build()))
                .profileBio(ProfileBio.builder().build())
                .keywords(Set.of("value"))
                .build();
        when(mockProfileInitService.initFromAuth(UUID.fromString("70a33fee-d415-4324-a1de-a3117d5aa691")))
                .thenReturn(profile);

        // Run the test
        final PersonalInfo result = profileServiceUnderTest.getInfo(
                UUID.fromString("70a33fee-d415-4324-a1de-a3117d5aa691"));

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testUpdateSkills() {
        // Setup
        UUID profileId = UUID.fromString("36330c3d-0c5b-4d3e-9d42-8376558ef8eb");
        final SkillCommand skillCommand = new SkillCommand(profileId,
                Set.of(new SkillPayload("name", 0, ProfileSkill.SkillLevel.ONE_2_THREE, false)));
        final ConnectProfile expectedResult = ConnectProfile.builder()
                .id(profileId)
                .personalInfo(PersonalInfo.builder()
                        .slug("slug")
                        .build())
                .profileSkills(Set.of(ProfileSkill.builder()
                        .id(UUID.fromString("52c26f01-86bd-4eab-97ee-d7d28b4c036f"))
                        .top(false)
                        .build()))
                .certifications(Set.of(Certification.builder().build()))
                .availabilities(Set.of(Availability.builder().build()))
                .profileBio(ProfileBio.builder().build())
                .keywords(Set.of("value"))
                .build();

        // Configure ProfileRepo.findById(...).
        final Optional<ConnectProfile> connectProfile = Optional.of(ConnectProfile.builder()
                .id(profileId)
                .personalInfo(PersonalInfo.builder()
                        .slug("slug")
                        .build())
                .profileSkills(Set.of(ProfileSkill.builder()
                        .id(UUID.fromString("52c26f01-86bd-4eab-97ee-d7d28b4c036f"))
                        .top(false)
                        .build()))
                .certifications(Set.of(Certification.builder().build()))
                .availabilities(Set.of(Availability.builder().build()))
                .profileBio(ProfileBio.builder().build())
                .keywords(Set.of("value"))
                .build());
        when(mockProfileRepo.findById(profileId))
                .thenReturn(connectProfile);

        // Configure ProfileMapper.toProfileSkill(...).
        final ProfileSkill profileSkill = ProfileSkill.builder()
                .id(UUID.fromString("52c26f01-86bd-4eab-97ee-d7d28b4c036f"))
                .top(false)
                .build();
        when(mockProfileMapper.toProfileSkill(new SkillPayload("name", 0, ProfileSkill.SkillLevel.ONE_2_THREE, false),
                ConnectProfile.builder()
                        .id(profileId)
                        .personalInfo(PersonalInfo.builder()
                                .slug("slug")
                                .build())
                        .profileSkills(Set.of(ProfileSkill.builder()
                                .id(UUID.fromString("52c26f01-86bd-4eab-97ee-d7d28b4c036f"))
                                .top(false)
                                .build()))
                        .certifications(Set.of(Certification.builder().build()))
                        .availabilities(Set.of(Availability.builder().build()))
                        .profileBio(ProfileBio.builder().build())
                        .keywords(Set.of("value"))
                        .build())).thenReturn(profileSkill);

        when(mockProfileIndexingService.extractFrom(ConnectProfile.builder()
                .id(profileId)
                .personalInfo(PersonalInfo.builder()
                        .slug("slug")
                        .build())
                .profileSkills(Set.of(ProfileSkill.builder()
                        .id(UUID.fromString("52c26f01-86bd-4eab-97ee-d7d28b4c036f"))
                        .top(false)
                        .build()))
                .certifications(Set.of(Certification.builder().build()))
                .availabilities(Set.of(Availability.builder().build()))
                .profileBio(ProfileBio.builder().build())
                .keywords(Set.of("value"))
                .build())).thenReturn(Set.of("value"));

        // Configure ProfileRepo.save(...).
        final ConnectProfile profile = ConnectProfile.builder()
                .id(profileId)
                .personalInfo(PersonalInfo.builder()
                        .slug("slug")
                        .build())
                .profileSkills(Set.of(ProfileSkill.builder()
                        .id(UUID.fromString("52c26f01-86bd-4eab-97ee-d7d28b4c036f"))
                        .top(false)
                        .build()))
                .certifications(Set.of(Certification.builder().build()))
                .availabilities(Set.of(Availability.builder().build()))
                .profileBio(ProfileBio.builder().build())
                .keywords(Set.of("value"))
                .build();
        when(mockProfileRepo.save(profile)).thenReturn(profile);

        // Run the test
        final ConnectProfile result = profileServiceUnderTest.updateSkills(skillCommand);

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
        verify(mockProfileSkillRepo).deleteByProfileId(profileId);
    }

    @Test
    void testGetSkills() {
        // Setup
        final Set<SkillReadPayload> expectedResult = Set.of(
                new SkillReadPayload(UUID.fromString("fcd1f8e0-235e-4232-a556-5f8e8f4c7941"),
                        new SkillPayload("name", 0, ProfileSkill.SkillLevel.ONE_2_THREE, false)));

        // Configure ProfileSkillRepo.findByProfileId(...).
        final Set<ProfileSkill> profileSkills = Set.of(ProfileSkill.builder()
                .id(UUID.fromString("52c26f01-86bd-4eab-97ee-d7d28b4c036f"))
                .top(false)
                .build());
        when(mockProfileSkillRepo.findByProfileId(UUID.fromString("4f2e0674-6b74-40ca-a5c7-10c17dddda66")))
                .thenReturn(profileSkills);

        // Configure ProfileMapper.toSkillReadPayload(...).
        final SkillReadPayload skillReadPayload = new SkillReadPayload(
                UUID.fromString("0742c5ce-54bf-42e4-8b0f-7b28428a38d4"),
                new SkillPayload("name", 0, ProfileSkill.SkillLevel.ONE_2_THREE, false));
        when(mockProfileMapper.toSkillReadPayload(ProfileSkill.builder()
                .id(UUID.fromString("52c26f01-86bd-4eab-97ee-d7d28b4c036f"))
                .top(false)
                .build())).thenReturn(skillReadPayload);

        // Run the test
        final Set<SkillReadPayload> result = profileServiceUnderTest.getSkills(
                UUID.fromString("4f2e0674-6b74-40ca-a5c7-10c17dddda66"));

        // Verify the results
        assertThat(result)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expectedResult);
    }

    @Test
    void testGetSkills_ProfileSkillRepoReturnsNoItems() {
        // Setup
        when(mockProfileSkillRepo.findByProfileId(UUID.fromString("4f2e0674-6b74-40ca-a5c7-10c17dddda66")))
                .thenReturn(Collections.emptySet());

        // Run the test
        final Set<SkillReadPayload> result = profileServiceUnderTest.getSkills(
                UUID.fromString("4f2e0674-6b74-40ca-a5c7-10c17dddda66"));

        // Verify the results
        assertThat(result).isEqualTo(Collections.emptySet());
    }

    @Test
    void testUpdateCertifications() {
        // Setup
        UUID profileId = UUID.fromString("7629d7d1-2d23-4c0a-8234-e0afe28f8224");
        final CertificationsCommand certificationsCommand = new CertificationsCommand(
                profileId,
                Set.of(new CertificationPayload("title", 2020)));
        final ConnectProfile expectedResult = ConnectProfile.builder()
                .id(profileId)
                .personalInfo(PersonalInfo.builder()
                        .slug("slug")
                        .build())
                .profileSkills(Set.of(ProfileSkill.builder()
                        .id(UUID.fromString("52c26f01-86bd-4eab-97ee-d7d28b4c036f"))
                        .top(false)
                        .build()))
                .certifications(Set.of(Certification.builder().build()))
                .availabilities(Set.of(Availability.builder().build()))
                .profileBio(ProfileBio.builder().build())
                .keywords(Set.of("value"))
                .build();

        // Configure ProfileRepo.findById(...).
        final Optional<ConnectProfile> connectProfile = Optional.of(ConnectProfile.builder()
                .id(profileId)
                .personalInfo(PersonalInfo.builder()
                        .slug("slug")
                        .build())
                .profileSkills(Set.of(ProfileSkill.builder()
                        .id(UUID.fromString("52c26f01-86bd-4eab-97ee-d7d28b4c036f"))
                        .top(false)
                        .build()))
                .certifications(Set.of(Certification.builder().build()))
                .availabilities(Set.of(Availability.builder().build()))
                .profileBio(ProfileBio.builder().build())
                .keywords(Set.of("value"))
                .build());
        when(mockProfileRepo.findById(profileId))
                .thenReturn(connectProfile);

        when(mockProfileMapper.toCertification(new CertificationPayload("title", 2020), ConnectProfile.builder()
                .id(profileId)
                .personalInfo(PersonalInfo.builder()
                        .slug("slug")
                        .build())
                .profileSkills(Set.of(ProfileSkill.builder()
                        .id(UUID.fromString("52c26f01-86bd-4eab-97ee-d7d28b4c036f"))
                        .top(false)
                        .build()))
                .certifications(Set.of(Certification.builder().build()))
                .availabilities(Set.of(Availability.builder().build()))
                .profileBio(ProfileBio.builder().build())
                .keywords(Set.of("value"))
                .build())).thenReturn(Certification.builder().build());
        when(mockProfileIndexingService.extractFrom(ConnectProfile.builder()
                .id(profileId)
                .personalInfo(PersonalInfo.builder()
                        .slug("slug")
                        .build())
                .profileSkills(Set.of(ProfileSkill.builder()
                        .id(UUID.fromString("52c26f01-86bd-4eab-97ee-d7d28b4c036f"))
                        .top(false)
                        .build()))
                .certifications(Set.of(Certification.builder().build()))
                .availabilities(Set.of(Availability.builder().build()))
                .profileBio(ProfileBio.builder().build())
                .keywords(Set.of("value"))
                .build())).thenReturn(Set.of("value"));

        // Configure ProfileRepo.save(...).
        final ConnectProfile profile = ConnectProfile.builder()
                .id(profileId)
                .personalInfo(PersonalInfo.builder()
                        .slug("slug")
                        .build())
                .profileSkills(Set.of(ProfileSkill.builder()
                        .id(UUID.fromString("52c26f01-86bd-4eab-97ee-d7d28b4c036f"))
                        .top(false)
                        .build()))
                .certifications(Set.of(Certification.builder().build()))
                .availabilities(Set.of(Availability.builder().build()))
                .profileBio(ProfileBio.builder().build())
                .keywords(Set.of("value"))
                .build();
        when(mockProfileRepo.save(ConnectProfile.builder()
                .id(profileId)
                .personalInfo(PersonalInfo.builder()
                        .slug("slug")
                        .build())
                .profileSkills(Set.of(ProfileSkill.builder()
                        .id(UUID.fromString("52c26f01-86bd-4eab-97ee-d7d28b4c036f"))
                        .top(false)
                        .build()))
                .certifications(Set.of(Certification.builder().build()))
                .availabilities(Set.of(Availability.builder().build()))
                .profileBio(ProfileBio.builder().build())
                .keywords(Set.of("value"))
                .build())).thenReturn(profile);

        // Run the test
        final ConnectProfile result = profileServiceUnderTest.updateCertifications(certificationsCommand);

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
        verify(mockCertificationRepo).deleteByProfileId(profileId);
    }

    @Test
    void testGetCertifications() {
        // Setup
        final Set<CertificationPayload> expectedResult = Set.of(new CertificationPayload("title", 2020));
        when(mockCertificationRepo.findByProfileId(UUID.fromString("2394252c-476f-4228-b32f-f1b5e73394b0")))
                .thenReturn(Set.of(Certification.builder().build()));
        when(mockProfileMapper.toCertPayload(Certification.builder().build()))
                .thenReturn(new CertificationPayload("title", 2020));

        // Run the test
        final Set<CertificationPayload> result = profileServiceUnderTest.getCertifications(
                UUID.fromString("2394252c-476f-4228-b32f-f1b5e73394b0"));

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testGetCertifications_CertificationRepoReturnsNoItems() {
        // Setup
        when(mockCertificationRepo.findByProfileId(UUID.fromString("2394252c-476f-4228-b32f-f1b5e73394b0")))
                .thenReturn(Collections.emptySet());

        // Run the test
        final Set<CertificationPayload> result = profileServiceUnderTest.getCertifications(
                UUID.fromString("2394252c-476f-4228-b32f-f1b5e73394b0"));

        // Verify the results
        assertThat(result).isEqualTo(Collections.emptySet());
    }

    @Test
    void testSave() {
        // Setup
        final ConnectProfile profile = ConnectProfile.builder()
                .id(UUID.fromString("8d2b6d88-079b-40a0-9751-bfe2d2e6fa1b"))
                .personalInfo(PersonalInfo.builder()
                        .slug("slug")
                        .build())
                .profileSkills(Set.of(ProfileSkill.builder()
                        .id(UUID.fromString("52c26f01-86bd-4eab-97ee-d7d28b4c036f"))
                        .top(false)
                        .build()))
                .certifications(Set.of(Certification.builder().build()))
                .availabilities(Set.of(Availability.builder().build()))
                .profileBio(ProfileBio.builder().build())
                .keywords(Set.of("value"))
                .build();
        final ConnectProfile expectedResult = ConnectProfile.builder()
                .id(UUID.fromString("8d2b6d88-079b-40a0-9751-bfe2d2e6fa1b"))
                .personalInfo(PersonalInfo.builder()
                        .slug("slug")
                        .build())
                .profileSkills(Set.of(ProfileSkill.builder()
                        .id(UUID.fromString("52c26f01-86bd-4eab-97ee-d7d28b4c036f"))
                        .top(false)
                        .build()))
                .certifications(Set.of(Certification.builder().build()))
                .availabilities(Set.of(Availability.builder().build()))
                .profileBio(ProfileBio.builder().build())
                .keywords(Set.of("value"))
                .build();
        when(mockProfileIndexingService.extractFrom(ConnectProfile.builder()
                .id(UUID.fromString("8d2b6d88-079b-40a0-9751-bfe2d2e6fa1b"))
                .personalInfo(PersonalInfo.builder()
                        .slug("slug")
                        .build())
                .profileSkills(Set.of(ProfileSkill.builder()
                        .id(UUID.fromString("52c26f01-86bd-4eab-97ee-d7d28b4c036f"))
                        .top(false)
                        .build()))
                .certifications(Set.of(Certification.builder().build()))
                .availabilities(Set.of(Availability.builder().build()))
                .profileBio(ProfileBio.builder().build())
                .keywords(Set.of("value"))
                .build())).thenReturn(Set.of("value"));

        // Configure ProfileRepo.save(...).
        final ConnectProfile profile1 = ConnectProfile.builder()
                .id(UUID.fromString("8d2b6d88-079b-40a0-9751-bfe2d2e6fa1b"))
                .personalInfo(PersonalInfo.builder()
                        .slug("slug")
                        .build())
                .profileSkills(Set.of(ProfileSkill.builder()
                        .id(UUID.fromString("52c26f01-86bd-4eab-97ee-d7d28b4c036f"))
                        .top(false)
                        .build()))
                .certifications(Set.of(Certification.builder().build()))
                .availabilities(Set.of(Availability.builder().build()))
                .profileBio(ProfileBio.builder().build())
                .keywords(Set.of("value"))
                .build();
        when(mockProfileRepo.save(ConnectProfile.builder()
                .id(UUID.fromString("8d2b6d88-079b-40a0-9751-bfe2d2e6fa1b"))
                .personalInfo(PersonalInfo.builder()
                        .slug("slug")
                        .build())
                .profileSkills(Set.of(ProfileSkill.builder()
                        .id(UUID.fromString("52c26f01-86bd-4eab-97ee-d7d28b4c036f"))
                        .top(false)
                        .build()))
                .certifications(Set.of(Certification.builder().build()))
                .availabilities(Set.of(Availability.builder().build()))
                .profileBio(ProfileBio.builder().build())
                .keywords(Set.of("value"))
                .build())).thenReturn(profile1);

        // Run the test
        final ConnectProfile result = profileServiceUnderTest.save(profile);

        // Verify the results
        assertThat(result)
                .usingRecursiveComparison()
                .ignoringFields("id", "skills.id")
                .isEqualTo(expectedResult);
    }

    @Test
    void testGetProfile() throws Exception {
        // Setup
        UUID profileId = UUID.fromString("6e1e2181-8d5a-4c7c-97a3-0f5cf7c0ee82");
        final ProfilePayload expectedResult = new ProfilePayload(
                profileId,
                null, PersonalInfo.builder()
                .slug("slug")
                .build(), 0.0, Set.of(new SkillReadPayload(UUID.fromString("808e5d0b-6250-4249-a323-a082c76d2314"),
                new SkillPayload("name", 0, ProfileSkill.SkillLevel.ONE_2_THREE, false))),
                Set.of(new CertificationPayload("title", 2020)), new ProfileMetrics(0L, 0L, 0L, 0L, 0L,
                ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC),
                ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC)), ProfileBio.builder().build(),
                Map.ofEntries(Map.entry(DayOfWeek.FRIDAY, AvailabilityType.MORNING)),
                new ProfileRatingPayload("sourceProfilePublicName", new URL("https://example.com/"),
                        UUID.fromString("4e93879b-cb87-4f43-9cf7-54efba72cd2b"), 0, "description",
                        ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC)),
                List.of(new ProfileRatingPayload("sourceProfilePublicName", new URL("https://example.com/"),
                        UUID.fromString("40931286-d7c4-487d-bdc9-36eb89c766e4"), 0, "description",
                        ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC))), ProfileType.USER);

        // Configure ProfileRepo.findById(...).
        final Optional<ConnectProfile> connectProfile = Optional.of(ConnectProfile.builder()
                .id(profileId)
                .personalInfo(PersonalInfo.builder()
                        .slug("slug")
                        .build())
                .profileSkills(Set.of(ProfileSkill.builder()
                        .id(UUID.fromString("52c26f01-86bd-4eab-97ee-d7d28b4c036f"))
                        .top(false)
                        .build()))
                .certifications(Set.of(Certification.builder().build()))
                .availabilities(Set.of(Availability.builder().build()))
                .profileBio(ProfileBio.builder().build())
                .keywords(Set.of("value"))
                .build());
        when(mockProfileRepo.findById(profileId))
                .thenReturn(connectProfile);

        when(mockRatingListService.getAverageFor(profileId))
                .thenReturn(0.0);

        // Configure ProfileMapper.toSkillReadPayload(...).
        final SkillReadPayload skillReadPayload = new SkillReadPayload(
                profileId,
                new SkillPayload("name", 0, ProfileSkill.SkillLevel.ONE_2_THREE, false));
        when(mockProfileMapper.toSkillReadPayload(ProfileSkill.builder()
                .id(UUID.fromString("52c26f01-86bd-4eab-97ee-d7d28b4c036f"))
                .top(false)
                .build())).thenReturn(skillReadPayload);

        // Configure ProfileMapper.toCertsPayload(...).
        final Set<CertificationPayload> certificationPayloads = Set.of(new CertificationPayload("title", 2020));
        when(mockProfileMapper.toCertsPayload(Set.of(Certification.builder().build())))
                .thenReturn(certificationPayloads);

        // Configure ProfileMetricsService.getMetricsFor(...).
        final Optional<ProfileMetrics> profileMetrics = Optional.of(new ProfileMetrics(0L, 0L, 0L, 0L, 0L,
                ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC),
                ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC)));
        when(mockProfileMetricsService.getMetricsFor(
                profileId)).thenReturn(profileMetrics);

        // Configure ProfileMapper.toAvailPayload(...).
        final Map<DayOfWeek, AvailabilityType> dayOfWeekAvailabilityTypeMap = Map.ofEntries(
                Map.entry(DayOfWeek.FRIDAY, AvailabilityType.MORNING));
        when(mockProfileMapper.toAvailPayload(Set.of(Availability.builder().build())))
                .thenReturn(dayOfWeekAvailabilityTypeMap);

        // Configure RatingListService.findRatingsFromTo(...).
        final Optional<ProfileRatingPayload> profileRatingPayload = Optional.of(
                new ProfileRatingPayload("sourceProfilePublicName", new URL("https://example.com/"),
                        UUID.fromString("920d3556-4698-4702-b817-f2e492a84929"), 0, "description",
                        ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC)));
        UUID loggedUser = UUID.fromString("756a6dc6-858e-4495-9ec0-52b68f975ed2");
        when(mockRatingListService.findRatingsFromTo(any(),
                any())).thenReturn(profileRatingPayload);

        // Configure RatingListService.findRatingsFor(...).
        final Page<ProfileRatingPayload> profileRatingPayloads = new PageImpl<>(
                List.of(new ProfileRatingPayload("sourceProfilePublicName", new URL("https://example.com/"),
                        UUID.fromString("a66dc175-8d09-4767-acd0-8b1935faba34"), 0, "description",
                        ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC))));
        when(mockRatingListService.findRatingsFor(any(),
                any(Pageable.class))).thenReturn(profileRatingPayloads);

        // Run the test
        final ProfilePayload result = profileServiceUnderTest.getProfile(
                profileId,
                loggedUser);

        // Verify the results
        assertThat(result)
                .usingRecursiveComparison()
                .ignoringFields("skills.id",
                        "lastRatings.sourceProfileId",
                        "myRating.sourceProfileId")
                .isEqualTo(expectedResult);
    }

    @Test
    void testUpdateAvailability() {
        // Setup
        UUID profileId = UUID.fromString("5178b692-b87f-4841-9937-d148047ddaac");
        final AvailabilityCommand availabilityCommand = new AvailabilityCommand(
                profileId,
                Map.ofEntries(Map.entry(DayOfWeek.FRIDAY, AvailabilityType.ALL)));
        Set<Availability> availabilities = Set.of(Availability.builder()
                .dayOfWeek(DayOfWeek.FRIDAY)
                .availabilityType(AvailabilityType.ALL)
                .build());
        final ConnectProfile expectedResult = ConnectProfile.builder()
                .id(profileId)
                .personalInfo(PersonalInfo.builder()
                        .slug("slug")
                        .build())
                .profileSkills(Set.of(ProfileSkill.builder()
                        .id(UUID.fromString("52c26f01-86bd-4eab-97ee-d7d28b4c036f"))
                        .top(false)
                        .build()))
                .certifications(Set.of(Certification.builder().build()))
                .availabilities(availabilities)
                .profileBio(ProfileBio.builder().build())
                .keywords(Set.of("value"))
                .build();

        // Configure ProfileRepo.findById(...).
        final Optional<ConnectProfile> connectProfile = Optional.of(ConnectProfile.builder()
                .id(profileId)
                .personalInfo(PersonalInfo.builder()
                        .slug("slug")
                        .build())
                .profileSkills(Set.of(ProfileSkill.builder()
                        .id(UUID.fromString("52c26f01-86bd-4eab-97ee-d7d28b4c036f"))
                        .top(false)
                        .build()))
                .certifications(Set.of(Certification.builder().build()))
                .availabilities(availabilities)
                .profileBio(ProfileBio.builder().build())
                .keywords(Set.of("value"))
                .build());
        when(mockProfileRepo.findById(profileId))
                .thenReturn(connectProfile);

        when(mockProfileMapper.mapAvailabilities(Map.ofEntries(Map.entry(DayOfWeek.FRIDAY, AvailabilityType.ALL)),
                ConnectProfile.builder()
                        .id(profileId)
                        .personalInfo(PersonalInfo.builder()
                                .slug("slug")
                                .build())
                        .profileSkills(Set.of(ProfileSkill.builder()
                                .id(UUID.fromString("52c26f01-86bd-4eab-97ee-d7d28b4c036f"))
                                .top(false)
                                .build()))
                        .certifications(Set.of(Certification.builder().build()))
                        .availabilities(availabilities)
                        .profileBio(ProfileBio.builder().build())
                        .keywords(Set.of("value"))
                        .build())).thenReturn(availabilities);
        when(mockProfileIndexingService.extractFrom(any())).thenReturn(Set.of("value"));

        // Configure ProfileRepo.save(...).
        final ConnectProfile profile = ConnectProfile.builder()
                .id(profileId)
                .personalInfo(PersonalInfo.builder()
                        .slug("slug")
                        .build())
                .profileSkills(Set.of(ProfileSkill.builder()
                        .id(UUID.fromString("52c26f01-86bd-4eab-97ee-d7d28b4c036f"))
                        .top(false)
                        .build()))
                .certifications(Set.of(Certification.builder().build()))
                .availabilities(availabilities)
                .profileBio(ProfileBio.builder().build())
                .keywords(Set.of("value"))
                .build();
        when(mockProfileRepo.save(ConnectProfile.builder()
                .id(profileId)
                .personalInfo(PersonalInfo.builder()
                        .slug("slug")
                        .build())
                .profileSkills(Set.of(ProfileSkill.builder()
                        .id(UUID.fromString("52c26f01-86bd-4eab-97ee-d7d28b4c036f"))
                        .top(false)
                        .build()))
                .certifications(Set.of(Certification.builder().build()))
                .availabilities(availabilities)
                .profileBio(ProfileBio.builder().build())
                .keywords(Set.of("value"))
                .build())).thenReturn(profile);

        // Run the test
        final ConnectProfile result = profileServiceUnderTest.updateAvailability(availabilityCommand);

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
        verify(mockAvailabilityRepo).deleteByProfileId(profileId);
    }

    @Test
    void testGetAvailabilities() {
        // Setup
        final Map<DayOfWeek, AvailabilityType> expectedResult = Map.ofEntries(
                Map.entry(DayOfWeek.FRIDAY, AvailabilityType.ALL));
        Set<Availability> availabilities = Set.of(Availability.builder()
                .availabilityType(AvailabilityType.ALL)
                .dayOfWeek(DayOfWeek.FRIDAY)
                .build());
        when(mockAvailabilityRepo.findByProfileId(UUID.fromString("a4731c28-005e-4c97-ab6a-4561bc5d44bb")))
                .thenReturn(availabilities);

        // Configure ProfileMapper.toAvailPayload(...).
        final Map<DayOfWeek, AvailabilityType> dayOfWeekAvailabilityTypeMap = Map.ofEntries(
                Map.entry(DayOfWeek.FRIDAY, AvailabilityType.ALL));
        when(mockProfileMapper.toAvailPayload(availabilities))
                .thenReturn(dayOfWeekAvailabilityTypeMap);

        // Run the test
        final Map<DayOfWeek, AvailabilityType> result = profileServiceUnderTest.getAvailabilities(
                UUID.fromString("a4731c28-005e-4c97-ab6a-4561bc5d44bb"));

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testUpdateBio() {
        // Setup
        UUID profileId = UUID.fromString("ada8b4b6-ae3a-4ea7-9395-f33a1b165219");
        final BioCommand bioCommand = new BioCommand(profileId,
                ProfileBio.builder().build());
        final ConnectProfile expectedResult = ConnectProfile.builder()
                .id(profileId)
                .personalInfo(PersonalInfo.builder()
                        .slug("slug")
                        .build())
                .profileSkills(Set.of(ProfileSkill.builder()
                        .id(UUID.fromString("52c26f01-86bd-4eab-97ee-d7d28b4c036f"))
                        .top(false)
                        .build()))
                .certifications(Set.of(Certification.builder().build()))
                .availabilities(Set.of(Availability.builder().build()))
                .profileBio(ProfileBio.builder().build())
                .keywords(Set.of("value"))
                .build();

        // Configure ProfileRepo.findById(...).
        final Optional<ConnectProfile> connectProfile = Optional.of(ConnectProfile.builder()
                .id(profileId)
                .personalInfo(PersonalInfo.builder()
                        .slug("slug")
                        .build())
                .profileSkills(Set.of(ProfileSkill.builder()
                        .id(UUID.fromString("52c26f01-86bd-4eab-97ee-d7d28b4c036f"))
                        .top(false)
                        .build()))
                .certifications(Set.of(Certification.builder().build()))
                .availabilities(Set.of(Availability.builder().build()))
                .profileBio(ProfileBio.builder().build())
                .keywords(Set.of("value"))
                .build());
        when(mockProfileRepo.findById(profileId))
                .thenReturn(connectProfile);

        when(mockProfileIndexingService.extractFrom(ConnectProfile.builder()
                .id(profileId)
                .personalInfo(PersonalInfo.builder()
                        .slug("slug")
                        .build())
                .profileSkills(Set.of(ProfileSkill.builder()
                        .id(UUID.fromString("52c26f01-86bd-4eab-97ee-d7d28b4c036f"))
                        .top(false)
                        .build()))
                .certifications(Set.of(Certification.builder().build()))
                .availabilities(Set.of(Availability.builder().build()))
                .profileBio(ProfileBio.builder().build())
                .keywords(Set.of("value"))
                .build())).thenReturn(Set.of("value"));

        // Configure ProfileRepo.save(...).
        final ConnectProfile profile = ConnectProfile.builder()
                .id(profileId)
                .personalInfo(PersonalInfo.builder()
                        .slug("slug")
                        .build())
                .profileSkills(Set.of(ProfileSkill.builder()
                        .id(UUID.fromString("52c26f01-86bd-4eab-97ee-d7d28b4c036f"))
                        .top(false)
                        .build()))
                .certifications(Set.of(Certification.builder().build()))
                .availabilities(Set.of(Availability.builder().build()))
                .profileBio(ProfileBio.builder().build())
                .keywords(Set.of("value"))
                .build();
        when(mockProfileRepo.save(ConnectProfile.builder()
                .id(profileId)
                .personalInfo(PersonalInfo.builder()
                        .slug("slug")
                        .build())
                .profileSkills(Set.of(ProfileSkill.builder()
                        .id(UUID.fromString("52c26f01-86bd-4eab-97ee-d7d28b4c036f"))
                        .top(false)
                        .build()))
                .certifications(Set.of(Certification.builder().build()))
                .availabilities(Set.of(Availability.builder().build()))
                .profileBio(ProfileBio.builder().build())
                .keywords(Set.of("value"))
                .build())).thenReturn(profile);

        // Run the test
        final ConnectProfile result = profileServiceUnderTest.updateBio(bioCommand);

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testGetProfileBio() {
        // Setup
        final Optional<ProfileBio> expectedResult = Optional.of(ProfileBio.builder().build());

        // Configure ProfileRepo.findById(...).
        final Optional<ConnectProfile> connectProfile = Optional.of(ConnectProfile.builder()
                .id(UUID.fromString("8d2b6d88-079b-40a0-9751-bfe2d2e6fa1b"))
                .personalInfo(PersonalInfo.builder()
                        .slug("slug")
                        .build())
                .profileSkills(Set.of(ProfileSkill.builder()
                        .id(UUID.fromString("52c26f01-86bd-4eab-97ee-d7d28b4c036f"))
                        .top(false)
                        .build()))
                .certifications(Set.of(Certification.builder().build()))
                .availabilities(Set.of(Availability.builder().build()))
                .profileBio(ProfileBio.builder().build())
                .keywords(Set.of("value"))
                .build());
        when(mockProfileRepo.findById(UUID.fromString("64dfac34-804d-4051-bc9e-5cad68394b06")))
                .thenReturn(connectProfile);

        // Run the test
        final Optional<ProfileBio> result = profileServiceUnderTest.getProfileBio(
                UUID.fromString("64dfac34-804d-4051-bc9e-5cad68394b06"));

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testGetProfileBio_ProfileRepoReturnsAbsent() {
        // Setup
        when(mockProfileRepo.findById(UUID.fromString("64dfac34-804d-4051-bc9e-5cad68394b06")))
                .thenReturn(Optional.empty());

        // Run the test
        final Optional<ProfileBio> result = profileServiceUnderTest.getProfileBio(
                UUID.fromString("64dfac34-804d-4051-bc9e-5cad68394b06"));

        // Verify the results
        assertThat(result).isEmpty();
    }

    @Test
    void testPatchSkill() {
        // Setup
        UUID profileId = UUID.fromString("24eee31d-6582-4af1-bbf2-88fd09cf324f");
        UUID skillId = UUID.fromString("d2bcb1d6-28bf-4198-87a4-cf5e3d5d7490");
        final PatchSkillCommand patchSkillCommand = new PatchSkillCommand(
                profileId,
                skillId, new PatchSkillPayload(false));
        final ConnectProfile expectedResult = ConnectProfile.builder()
                .id(profileId)
                .personalInfo(PersonalInfo.builder()
                        .slug("slug")
                        .build())
                .profileSkills(Set.of(ProfileSkill.builder()
                        .id(UUID.fromString("52c26f01-86bd-4eab-97ee-d7d28b4c036f"))
                        .top(false)
                        .build()))
                .certifications(Set.of(Certification.builder().build()))
                .availabilities(Set.of(Availability.builder().build()))
                .profileBio(ProfileBio.builder().build())
                .keywords(Set.of("value"))
                .build();

        // Configure ProfileRepo.findById(...).
        final Optional<ConnectProfile> connectProfile = Optional.of(ConnectProfile.builder()
                .id(profileId)
                .personalInfo(PersonalInfo.builder()
                        .slug("slug")
                        .build())
                .profileSkills(Set.of(ProfileSkill.builder()
                        .id(UUID.fromString("52c26f01-86bd-4eab-97ee-d7d28b4c036f"))
                        .top(false)
                        .build()))
                .certifications(Set.of(Certification.builder().build()))
                .availabilities(Set.of(Availability.builder().build()))
                .profileBio(ProfileBio.builder().build())
                .keywords(Set.of("value"))
                .build());
        when(mockProfileRepo.findById(profileId))
                .thenReturn(connectProfile);

        // Configure ProfileMapper.toSkillPayload(...).
        final SkillPayload skillPayload = new SkillPayload("name", 0, ProfileSkill.SkillLevel.ONE_2_THREE, false);
        when(mockProfileMapper.toSkillPayload(ProfileSkill.builder()
                .id(UUID.fromString("52c26f01-86bd-4eab-97ee-d7d28b4c036f"))
                .top(false)
                .build())).thenReturn(skillPayload);

        // Configure ProfileMapper.toProfileSkill(...).
        final ProfileSkill profileSkill = ProfileSkill.builder()
                .id(UUID.fromString("52c26f01-86bd-4eab-97ee-d7d28b4c036f"))
                .top(false)
                .build();
        when(mockProfileMapper.toProfileSkill(new SkillPayload("name", 0, ProfileSkill.SkillLevel.ONE_2_THREE, false),
                ConnectProfile.builder()
                        .id(profileId)
                        .personalInfo(PersonalInfo.builder()
                                .slug("slug")
                                .build())
                        .profileSkills(Set.of(ProfileSkill.builder()
                                .id(UUID.fromString("52c26f01-86bd-4eab-97ee-d7d28b4c036f"))
                                .top(false)
                                .build()))
                        .certifications(Set.of(Certification.builder().build()))
                        .availabilities(Set.of(Availability.builder().build()))
                        .profileBio(ProfileBio.builder().build())
                        .keywords(Set.of("value"))
                        .build())).thenReturn(profileSkill);

        when(mockProfileIndexingService.extractFrom(any())).thenReturn(Set.of("value"));

        // Configure ProfileRepo.save(...).
        final ConnectProfile profile = ConnectProfile.builder()
                .id(profileId)
                .personalInfo(PersonalInfo.builder()
                        .slug("slug")
                        .build())
                .profileSkills(Set.of(ProfileSkill.builder()
                        .id(UUID.fromString("52c26f01-86bd-4eab-97ee-d7d28b4c036f"))
                        .top(false)
                        .build()))
                .certifications(Set.of(Certification.builder().build()))
                .availabilities(Set.of(Availability.builder().build()))
                .profileBio(ProfileBio.builder().build())
                .keywords(Set.of("value"))
                .build();
        when(mockProfileRepo.save(ConnectProfile.builder()
                .id(profileId)
                .personalInfo(PersonalInfo.builder()
                        .slug("slug")
                        .build())
                .profileSkills(Set.of(ProfileSkill.builder()
                        .id(UUID.fromString("52c26f01-86bd-4eab-97ee-d7d28b4c036f"))
                        .top(false)
                        .build()))
                .certifications(Set.of(Certification.builder().build()))
                .availabilities(Set.of(Availability.builder().build()))
                .profileBio(ProfileBio.builder().build())
                .keywords(Set.of("value"))
                .build())).thenReturn(profile);

        // Run the test
        final ConnectProfile result = profileServiceUnderTest.patchSkill(patchSkillCommand);

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
        verify(mockProfileSkillRepo).deleteByProfileId(profileId);
    }

    @Test
    void testFindOrFail() {
        // Setup
        final ConnectProfile expectedResult = ConnectProfile.builder()
                .id(UUID.fromString("8d2b6d88-079b-40a0-9751-bfe2d2e6fa1b"))
                .personalInfo(PersonalInfo.builder()
                        .slug("slug")
                        .build())
                .profileSkills(Set.of(ProfileSkill.builder()
                        .id(UUID.fromString("52c26f01-86bd-4eab-97ee-d7d28b4c036f"))
                        .top(false)
                        .build()))
                .certifications(Set.of(Certification.builder().build()))
                .availabilities(Set.of(Availability.builder().build()))
                .profileBio(ProfileBio.builder().build())
                .keywords(Set.of("value"))
                .build();

        // Configure ProfileRepo.findById(...).
        final Optional<ConnectProfile> connectProfile = Optional.of(ConnectProfile.builder()
                .id(UUID.fromString("8d2b6d88-079b-40a0-9751-bfe2d2e6fa1b"))
                .personalInfo(PersonalInfo.builder()
                        .slug("slug")
                        .build())
                .profileSkills(Set.of(ProfileSkill.builder()
                        .id(UUID.fromString("52c26f01-86bd-4eab-97ee-d7d28b4c036f"))
                        .top(false)
                        .build()))
                .certifications(Set.of(Certification.builder().build()))
                .availabilities(Set.of(Availability.builder().build()))
                .profileBio(ProfileBio.builder().build())
                .keywords(Set.of("value"))
                .build());
        when(mockProfileRepo.findById(UUID.fromString("e180997c-289c-4c7a-8592-92c8b7d92955")))
                .thenReturn(connectProfile);

        // Run the test
        final ConnectProfile result = profileServiceUnderTest.findOrFail(
                UUID.fromString("e180997c-289c-4c7a-8592-92c8b7d92955"));

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testFindOrFail_ProfileRepoReturnsAbsent() {
        // Setup
        when(mockProfileRepo.findById(UUID.fromString("e180997c-289c-4c7a-8592-92c8b7d92955")))
                .thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(() -> profileServiceUnderTest.findOrFail(
                UUID.fromString("e180997c-289c-4c7a-8592-92c8b7d92955"))).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void testFailIfEmailExists() {
        // Setup
        when(mockProfileRepo.existsByPersonalInfoEmail("email")).thenReturn(false);

        // Run the test
        profileServiceUnderTest.failIfEmailExists("email");

        // Verify the results
    }

    @Test
    void testFailIfEmailExists_ProfileRepoReturnsTrue() {
        // Setup
        when(mockProfileRepo.existsByPersonalInfoEmail("email")).thenReturn(true);

        // Run the test
        assertThatThrownBy(() -> profileServiceUnderTest.failIfEmailExists("email"))
                .isInstanceOf(BadRequestException.class);
    }
}
