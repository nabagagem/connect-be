package com.nabagagem.connectbe.services.profile;

import com.nabagagem.connectbe.controllers.LoginHelper;
import com.nabagagem.connectbe.domain.profile.AvailabilityType;
import com.nabagagem.connectbe.domain.profile.ProfileMetrics;
import com.nabagagem.connectbe.domain.profile.ProfilePayload;
import com.nabagagem.connectbe.domain.profile.SkillPayload;
import com.nabagagem.connectbe.domain.profile.SkillReadPayload;
import com.nabagagem.connectbe.entities.CertificationPayload;
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
import org.springframework.security.access.AccessDeniedException;

import java.time.DayOfWeek;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProfileProfileInitServiceTest {

    @Mock
    private ProfileRepo mockProfileRepo;
    @Mock
    private LoginHelper mockLoginHelper;

    private ProfileAuthService profileAuthServiceUnderTest;

    @BeforeEach
    void setUp() {
        profileAuthServiceUnderTest = new ProfileAuthService(mockProfileRepo, mockLoginHelper);
    }

    private final ProfilePayload profile = new ProfilePayload(UUID.fromString("74632107-c565-4590-9395-49b68d14f694"),
            UUID.fromString("0491651f-03c0-4994-b965-838966b1c669"), PersonalInfo.builder()
            .publicProfile(false)
            .build(), Set.of(new SkillReadPayload(UUID.fromString("92520c30-2971-458c-a5ff-3e257aae9257"),
            new SkillPayload("name", 0, ProfileSkill.SkillLevel.ONE_2_THREE, false))),
            Set.of(new CertificationPayload("title", 2020)), ProfileBio.builder().build(),
            Map.ofEntries(Map.entry(DayOfWeek.FRIDAY, AvailabilityType.MORNING)), Map.of(), new ProfileMetrics(ZonedDateTime.now(), ZonedDateTime.now()), ProfileType.USER);

    @Test
    void testIsAllowedOn() {
        // Setup
        ProfilePayload profile = new ProfilePayload(UUID.fromString("74632107-c565-4590-9395-49b68d14f694"),
                UUID.fromString("0491651f-03c0-4994-b965-838966b1c669"), PersonalInfo.builder()
                .publicProfile(true)
                .build(), Set.of(new SkillReadPayload(UUID.fromString("92520c30-2971-458c-a5ff-3e257aae9257"),
                new SkillPayload("name", 0, ProfileSkill.SkillLevel.ONE_2_THREE, false))),
                Set.of(new CertificationPayload("title", 2020)), ProfileBio.builder().build(),
                Map.ofEntries(Map.entry(DayOfWeek.FRIDAY, AvailabilityType.MORNING)), Map.of(), new ProfileMetrics(ZonedDateTime.now(), ZonedDateTime.now()),
                ProfileType.USER);

        // Run the test
        final ProfilePayload result = profileAuthServiceUnderTest.isAllowedOn(profile);

        // Verify the results
        assertThat(result).isEqualTo(profile);
    }

    @Test
    void testIsAllowedOn_LoginHelperReturnsAbsent() {
        // Setup
        when(mockLoginHelper.loggedUser()).thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(() -> profileAuthServiceUnderTest.isAllowedOn(profile))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void testFailIfNotLoggedIn() {
        // Setup
        // Configure LoginHelper.loggedUser(...).
        final Optional<UUID> uuid = Optional.of(UUID.fromString("0ad10c2e-891b-4193-a055-2e5403184f34"));
        when(mockLoginHelper.loggedUser()).thenReturn(uuid);

        when(mockProfileRepo.isAltFrom(UUID.fromString("b255f6d7-a150-4b3d-b42d-f41b4a257f3d"),
                uuid.get())).thenReturn(true);

        // Run the test
        profileAuthServiceUnderTest.failIfNotLoggedIn(UUID.fromString("b255f6d7-a150-4b3d-b42d-f41b4a257f3d"));

        // Verify the results
    }

    @Test
    void testFailIfNotLoggedIn_LoginHelperReturnsAbsent() {
        // Setup
        when(mockLoginHelper.loggedUser()).thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(() -> profileAuthServiceUnderTest.failIfNotLoggedIn(
                UUID.fromString("b255f6d7-a150-4b3d-b42d-f41b4a257f3d"))).isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void testFailIfNotLoggedIn_ProfileRepoReturnsFalse() {
        // Setup
        // Configure LoginHelper.loggedUser(...).
        UUID loggedUserId = UUID.fromString("0ad10c2e-891b-4193-a055-2e5403184f34");
        final Optional<UUID> uuid = Optional.of(loggedUserId);
        when(mockLoginHelper.loggedUser()).thenReturn(uuid);

        when(mockProfileRepo.isAltFrom(UUID.fromString("b255f6d7-a150-4b3d-b42d-f41b4a257f3d"),
                loggedUserId)).thenReturn(false);

        // Run the test
        assertThatThrownBy(() -> profileAuthServiceUnderTest.failIfNotLoggedIn(
                UUID.fromString("b255f6d7-a150-4b3d-b42d-f41b4a257f3d"))).isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void testFailIfNotCurrentProfile() {
        // Setup
        // Configure LoginHelper.loggedUser(...).
        final Optional<UUID> uuid = Optional.of(UUID.fromString("fee25bf2-3d0a-4738-aea3-2159a75e909e"));
        when(mockLoginHelper.loggedUser()).thenReturn(uuid);

        // Run the test
        profileAuthServiceUnderTest.failIfNotCurrentProfile(UUID.fromString("fee25bf2-3d0a-4738-aea3-2159a75e909e"));

        // Verify the results
    }

    @Test
    void testFailIfNotCurrentProfile_LoginHelperReturnsAbsent() {
        // Setup
        when(mockLoginHelper.loggedUser()).thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(() -> profileAuthServiceUnderTest.failIfNotCurrentProfile(
                UUID.fromString("462032a4-ef48-4f2e-8308-1b82ea6ce75e"))).isInstanceOf(AccessDeniedException.class);
    }
}
