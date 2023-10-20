package com.nabagagem.connectbe.services.profile;

import com.nabagagem.connectbe.domain.profile.ProfileGeoInfoCommand;
import com.nabagagem.connectbe.entities.Availability;
import com.nabagagem.connectbe.entities.Certification;
import com.nabagagem.connectbe.entities.ConnectProfile;
import com.nabagagem.connectbe.entities.GeoInfo;
import com.nabagagem.connectbe.entities.PersonalInfo;
import com.nabagagem.connectbe.entities.ProfileBio;
import com.nabagagem.connectbe.entities.ProfileSkill;
import com.nabagagem.connectbe.entities.ProfileType;
import com.nabagagem.connectbe.repos.AvailabilityRepo;
import com.nabagagem.connectbe.repos.CertificationRepo;
import com.nabagagem.connectbe.repos.ProfileRepo;
import com.nabagagem.connectbe.repos.ProfileSkillRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProfileServiceGeoTest {

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
    private ProfileIndexingService mockProfileIndexingService;

    private ProfileService profileServiceUnderTest;

    @BeforeEach
    void setUp() throws Exception {
        profileServiceUnderTest = new ProfileService(mockProfileRepo, mockProfileSkillRepo, mockCertificationRepo,
                mockProfileMapper, mockAvailabilityRepo, mockProfileInitService, mockProfileIndexingService);
    }


    @Test
    void testUpdateGeoInfo() {
        // Setup
        GeoInfo geoInfo = GeoInfo.builder()
                .latitude(0.0)
                .latitude(0.69)
                .build();
        UUID profileId = UUID.fromString("8c26b3ee-3dbd-4921-9d0a-7f31797747dd");
        final ProfileGeoInfoCommand profileGeoInfoCommand = new ProfileGeoInfoCommand(
                profileId, geoInfo);
        final ConnectProfile expectedResult = ConnectProfile.builder()
                .id(profileId)
                .personalInfo(PersonalInfo.builder()
                        .slug("slug")
                        .ready(false)
                        .build())
                .profileSkills(Set.of(ProfileSkill.builder()
                        .id(UUID.fromString("ff4c8d02-0935-4128-bb44-4c5d5df8d005"))
                        .top(false)
                        .build()))
                .certifications(Set.of(Certification.builder().build()))
                .availabilities(Set.of(Availability.builder().build()))
                .profileBio(ProfileBio.builder().build())
                .geoInfo(geoInfo)
                .profileType(ProfileType.USER)
                .keywords(Set.of("value"))
                .build();

        // Configure ProfileRepo.findById(...).
        ConnectProfile profile = ConnectProfile.builder()
                .id(profileId)
                .personalInfo(PersonalInfo.builder()
                        .slug("slug")
                        .ready(false)
                        .build())
                .profileSkills(Set.of(ProfileSkill.builder()
                        .id(UUID.fromString("ff4c8d02-0935-4128-bb44-4c5d5df8d005"))
                        .top(false)
                        .build()))
                .certifications(Set.of(Certification.builder().build()))
                .availabilities(Set.of(Availability.builder().build()))
                .profileBio(ProfileBio.builder().build())
                .geoInfo(geoInfo)
                .profileType(ProfileType.USER)
                .keywords(Set.of("value"))
                .build();
        final Optional<ConnectProfile> connectProfile = Optional.of(profile);
        when(mockProfileRepo.findById(profileId))
                .thenReturn(connectProfile);

        when(mockProfileIndexingService.extractFrom(profile))
                .thenReturn(Set.of("value"));

        // Configure ProfileRepo.save(...).
        when(mockProfileRepo.save(profile)).thenReturn(profile);

        // Run the test
        final ConnectProfile result = profileServiceUnderTest.updateGeoInfo(profileGeoInfoCommand);

        // Verify the results
        assertThat(result)
                .isEqualTo(expectedResult)
                .hasFieldOrPropertyWithValue("geoInfo", geoInfo);
    }
}
