package com.nabagagem.connectbe.services.profile;

import com.nabagagem.connectbe.domain.profile.AvailabilityType;
import com.nabagagem.connectbe.domain.profile.ProfilePayload;
import com.nabagagem.connectbe.domain.profile.SkillPayload;
import com.nabagagem.connectbe.domain.profile.SkillReadPayload;
import com.nabagagem.connectbe.entities.CertificationPayload;
import com.nabagagem.connectbe.entities.ConnectProfile;
import com.nabagagem.connectbe.entities.PersonalInfo;
import com.nabagagem.connectbe.entities.ProfileBio;
import com.nabagagem.connectbe.entities.ProfileSkill;
import com.nabagagem.connectbe.entities.ProfileType;
import com.nabagagem.connectbe.repos.ProfileRepo;
import com.nabagagem.connectbe.services.mappers.ProfilePayloadMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MyProfileServiceTest {

    @Mock
    private ProfileRepo mockProfileRepo;
    @Mock
    private ProfilePayloadMapper mockProfilePayloadMapper;

    private MyProfileService myProfileServiceUnderTest;

    @BeforeEach
    void setUp() throws Exception {
        myProfileServiceUnderTest = new MyProfileService(mockProfileRepo, mockProfilePayloadMapper);
    }

    @Test
    void testGetMyProfileFor() {
        // Setup
        UUID profileId = UUID.fromString("18663af0-67ca-4985-8bd9-30211964906a");
        ConnectProfile profile = ConnectProfile.builder()
                .id(profileId)
                .build();
        final ProfilePayload expectedResult = new ProfilePayload(
                UUID.fromString("2d169320-0d3d-4e5e-b50d-26536dcc6d31"),
                UUID.fromString("b8c2b335-a735-4656-8580-c0ebc057e607"), PersonalInfo.builder().build(),
                Set.of(new SkillReadPayload(UUID.fromString("615ca1ab-2dc2-498a-bd11-563022e76f62"),
                        new SkillPayload("name", 0, ProfileSkill.SkillLevel.ONE_2_THREE, false))),
                Set.of(new CertificationPayload("title", 2020)), ProfileBio.builder().build(),
                Map.ofEntries(Map.entry(DayOfWeek.FRIDAY, AvailabilityType.MORNING)), ProfileType.USER, Set.of());
        when(mockProfileRepo.getMyProfile(profileId))
                .thenReturn(profile);

        // Configure ProfilePayloadMapper.toPayload(...).
        when(mockProfilePayloadMapper.toPayload(profile, true)).thenReturn(expectedResult);

        // Run the test
        final ProfilePayload result = myProfileServiceUnderTest.getMyProfileFor(
                profileId);

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }
}
