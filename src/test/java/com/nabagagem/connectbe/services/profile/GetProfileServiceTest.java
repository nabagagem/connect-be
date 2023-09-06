package com.nabagagem.connectbe.services.profile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nabagagem.connectbe.domain.profile.AvailabilityType;
import com.nabagagem.connectbe.domain.profile.ProfileMetrics;
import com.nabagagem.connectbe.domain.profile.ProfilePayload;
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
import com.nabagagem.connectbe.repos.ProfileRepo;
import com.nabagagem.connectbe.services.rating.RatingListService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.net.URL;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetProfileServiceTest {

    @Mock
    private ProfileRepo mockProfileRepo;
    @Mock
    private RatingListService mockRatingListService;
    @Mock
    private ProfileService mockProfileService;
    @Mock
    private ProfileMapper mockProfileMapper;
    @Mock
    private ProfileMetricsService mockProfileMetricsService;

    private GetProfileService getProfileServiceUnderTest;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @BeforeEach
    void setUp() throws Exception {
        getProfileServiceUnderTest = new GetProfileService(mockProfileRepo, mockRatingListService, mockProfileService,
                mockProfileMapper, mockProfileMetricsService);
    }

    @Test
    void testGetProfile() throws Exception {
        // Setup
        final ProfilePayload expectedResult = new ProfilePayload(
                UUID.fromString("59bd3133-8a52-4886-875e-75af59a54e76"),
                UUID.fromString("3aeae27b-377d-4eba-b766-9717942f599f"), PersonalInfo.builder().build(), 0.0,
                Set.of(new SkillReadPayload(UUID.fromString("2a7727d6-1351-4a85-bc5d-bed374e68522"),
                        new SkillPayload("name", 0, ProfileSkill.SkillLevel.ONE_2_THREE, false))),
                Set.of(new CertificationPayload("title", 2020)), new ProfileMetrics(0L, 0L, 0L, 0L, 0L,
                ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneId.of("UTC")),
                ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneId.of("UTC"))),
                ProfileBio.builder().build(), Map.ofEntries(Map.entry(DayOfWeek.FRIDAY, AvailabilityType.MORNING)),
                new ProfileRatingPayload("sourceProfilePublicName", new URL("https://example.com/"),
                        UUID.fromString("6a1f88ac-acc4-49d2-99eb-62ac982bd25e"), 0, "description",
                        ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneId.of("UTC"))),
                List.of(new ProfileRatingPayload("sourceProfilePublicName", new URL("https://example.com/"),
                        UUID.fromString("63ba1a1f-362e-4703-9c30-1c19ace7bce9"), 0, "description",
                        ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneId.of("UTC")))), ProfileType.USER);

        // Configure ProfileRepo.findById(...).
        final Optional<ConnectProfile> connectProfile = Optional.of(ConnectProfile.builder()
                .id(UUID.fromString("a9d2cb95-69d8-4d3c-87e7-7dc47109bf14"))
                .personalInfo(PersonalInfo.builder().build())
                .profileSkills(Set.of(ProfileSkill.builder().build()))
                .certifications(Set.of(Certification.builder().build()))
                .availabilities(Set.of(Availability.builder().build()))
                .profileBio(ProfileBio.builder().build())
                .profileType(ProfileType.USER)
                .build());
        when(mockProfileRepo.findById(UUID.fromString("59e2bd2e-2da0-42f0-b802-01aa047c0600")))
                .thenReturn(connectProfile);

        when(mockRatingListService.getAverageFor(UUID.fromString("59e2bd2e-2da0-42f0-b802-01aa047c0600")))
                .thenReturn(0.0);

        // Configure ProfileMapper.toSkillReadPayload(...).
        final SkillReadPayload skillReadPayload = new SkillReadPayload(
                UUID.fromString("a1b2ec95-96ba-4e3a-80b5-b7f24adb4997"),
                new SkillPayload("name", 0, ProfileSkill.SkillLevel.ONE_2_THREE, false));
        when(mockProfileMapper.toSkillReadPayload(ProfileSkill.builder().build())).thenReturn(skillReadPayload);

        // Configure ProfileMapper.toCertsPayload(...).
        final Set<CertificationPayload> certificationPayloads = Set.of(new CertificationPayload("title", 2020));
        when(mockProfileMapper.toCertsPayload(Set.of(Certification.builder().build())))
                .thenReturn(certificationPayloads);

        // Configure ProfileMetricsService.getMetricsFor(...).
        final Optional<ProfileMetrics> profileMetrics = Optional.of(new ProfileMetrics(0L, 0L, 0L, 0L, 0L,
                ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneId.of("UTC")),
                ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneId.of("UTC"))));
        when(mockProfileMetricsService.getMetricsFor(
                UUID.fromString("59e2bd2e-2da0-42f0-b802-01aa047c0600"))).thenReturn(profileMetrics);

        // Configure ProfileMapper.toAvailPayload(...).
        final Map<DayOfWeek, AvailabilityType> dayOfWeekAvailabilityTypeMap = Map.ofEntries(
                Map.entry(DayOfWeek.FRIDAY, AvailabilityType.MORNING));
        when(mockProfileMapper.toAvailPayload(Set.of(Availability.builder().build())))
                .thenReturn(dayOfWeekAvailabilityTypeMap);

        // Configure RatingListService.findRatingsFromTo(...).
        final Optional<ProfileRatingPayload> profileRatingPayload = Optional.of(
                new ProfileRatingPayload("sourceProfilePublicName", new URL("https://example.com/"),
                        UUID.fromString("bcfef91f-9b4a-4637-8935-850e35143e54"), 0, "description",
                        ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneId.of("UTC"))));
        when(mockRatingListService.findRatingsFromTo(UUID.fromString("d28d2798-b1a0-423c-8dda-9e6b5c8296ae"),
                UUID.fromString("59e2bd2e-2da0-42f0-b802-01aa047c0600"))).thenReturn(profileRatingPayload);

        // Configure RatingListService.findRatingsFor(...).
        final Page<ProfileRatingPayload> profileRatingPayloads = new PageImpl<>(
                List.of(new ProfileRatingPayload("sourceProfilePublicName", new URL("https://example.com/"),
                        UUID.fromString("1919a866-36cc-403c-b33b-196856045857"), 0, "description",
                        ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneId.of("UTC")))));
        when(mockRatingListService.findRatingsFor(eq(UUID.fromString("59e2bd2e-2da0-42f0-b802-01aa047c0600")),
                any(Pageable.class))).thenReturn(profileRatingPayloads);

        // Run the test
        final ProfilePayload result = getProfileServiceUnderTest.getProfile(
                UUID.fromString("59e2bd2e-2da0-42f0-b802-01aa047c0600"),
                UUID.fromString("d28d2798-b1a0-423c-8dda-9e6b5c8296ae"));

        // Verify the results
        assertThatJson(result).isEqualTo("""
                {"id":"a9d2cb95-69d8-4d3c-87e7-7dc47109bf14","parentId":null,
                "personalInfo":{"publicName":null,"slug":null,"profession":null,"highlightTitle":null,"profileCategory":null,
                "otherCategory":null,"workingMode":null,"city":null,"publicProfile":null,"available":null,"tags":null,
                "amountPerHour":null,"email":null,"enableMessageEmail":null},"averageStars":0.0,
                "skills":[{"id":"a1b2ec95-96ba-4e3a-80b5-b7f24adb4997","name":"name","certifications":0,"level":"ONE_2_THREE","top":false}],
                "certifications":[{"title":"title","year":2020}],"profileMetrics":{"finishedJobs":0,"hiredJobs":0,
                "workedHours":0,"recommendationsAmount":0,"votes":0,"lastActivity":1577836800.000000000,"firstLogin":1577836800.000000000},
                "bio":{"description":null,"professionalRecord":null},"availabilities":{"FRIDAY":"MORNING"},
                "myRating":{"sourceProfilePublicName":"sourceProfilePublicName","sourceProfilePicURL":"https://example.com/",
                "sourceProfileId":"bcfef91f-9b4a-4637-8935-850e35143e54","stars":0,"description":"description",
                "ratedAt":1577836800.000000000},"lastRatings":[{"sourceProfilePublicName":"sourceProfilePublicName",
                "sourceProfilePicURL":"https://example.com/","sourceProfileId":"1919a866-36cc-403c-b33b-196856045857",
                "stars":0,"description":"description","ratedAt":1577836800.000000000}],"profileType":"USER"}
                """);
    }
}
