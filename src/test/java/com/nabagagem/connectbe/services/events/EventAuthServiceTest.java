package com.nabagagem.connectbe.services.events;

import com.nabagagem.connectbe.entities.ConnectProfile;
import com.nabagagem.connectbe.entities.ProfileType;
import com.nabagagem.connectbe.repos.ProfileRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.TestingAuthenticationToken;

import java.security.Principal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventAuthServiceTest {

    @Mock
    private ProfileRepo mockProfileRepo;

    private EventAuthService eventAuthServiceUnderTest;

    @BeforeEach
    void setUp() {
        eventAuthServiceUnderTest = new EventAuthService(mockProfileRepo);
    }

    @Test
    void testFailIfUnableToManage() {
        // Setup
        UUID id = UUID.fromString("b6cdc60f-8c7b-4610-8ef5-a69ae4e6dd72");
        final Principal principal = new TestingAuthenticationToken(id, "pass", "ROLE_USER");

        // Configure ProfileRepo.findById(...).
        final Optional<ConnectProfile> connectProfile = Optional.of(ConnectProfile.builder()
                .profileType(ProfileType.ADMIN)
                .build());
        when(mockProfileRepo.findById(id))
                .thenReturn(connectProfile);

        // Run the test
        eventAuthServiceUnderTest.failIfUnableToManage(principal);

        // Verify the results
    }

    @Test
    void testFailIfUnableToManage_NonAdmin() {
        // Setup
        UUID id = UUID.fromString("b6cdc60f-8c7b-4610-8ef5-a69ae4e6dd72");
        final Principal principal = new TestingAuthenticationToken(id, "pass", "ROLE_USER");

        // Configure ProfileRepo.findById(...).
        final Optional<ConnectProfile> connectProfile = Optional.of(ConnectProfile.builder()
                .profileType(ProfileType.USER)
                .build());
        when(mockProfileRepo.findById(id))
                .thenReturn(connectProfile);

        // Run the test
        assertThatThrownBy(() -> eventAuthServiceUnderTest.failIfUnableToManage(principal))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void testFailIfUnableToManage_ProfileRepoReturnsAbsent() {
        // Setup
        UUID id = UUID.fromString("b6cdc60f-8c7b-4610-8ef5-a69ae4e6dd72");
        final Principal principal = new TestingAuthenticationToken(id, "pass", "ROLE_USER");
        when(mockProfileRepo.findById(id))
                .thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(() -> eventAuthServiceUnderTest.failIfUnableToManage(principal))
                .isInstanceOf(AccessDeniedException.class);
    }
}
