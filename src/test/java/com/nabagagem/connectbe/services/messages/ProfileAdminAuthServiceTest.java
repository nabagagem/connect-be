package com.nabagagem.connectbe.services.messages;

import com.nabagagem.connectbe.entities.ProfileType;
import com.nabagagem.connectbe.repos.ProfileRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProfileAdminAuthServiceTest {

    @Mock
    private ProfileRepo mockProfileRepo;

    private ProfileAdminAuthService profileAdminAuthServiceUnderTest;

    @BeforeEach
    void setUp() {
        profileAdminAuthServiceUnderTest = new ProfileAdminAuthService(mockProfileRepo);
    }

    @Test
    void testFailIfUnauthorizedToPatch() {
        // Setup
        when(mockProfileRepo.getProfileTypeFor(UUID.fromString("db1c371e-9ee9-492c-9b9c-1112a522e4a0")))
                .thenReturn(Optional.of(ProfileType.ADMIN));

        // Run the test
        profileAdminAuthServiceUnderTest.failIfUnauthorizedToPatch(
                UUID.fromString("db1c371e-9ee9-492c-9b9c-1112a522e4a0"));
    }

    @Test
    void testFailIfUnauthorizedToPatch_ProfileRepoReturnsAbsent() {
        // Setup
        when(mockProfileRepo.getProfileTypeFor(UUID.fromString("db1c371e-9ee9-492c-9b9c-1112a522e4a0")))
                .thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(() -> profileAdminAuthServiceUnderTest.failIfUnauthorizedToPatch(
                UUID.fromString("db1c371e-9ee9-492c-9b9c-1112a522e4a0"))).isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void testFailIfUnauthorizedToPatch_ProfileRepoReturnsNonAdmin() {
        // Setup
        when(mockProfileRepo.getProfileTypeFor(UUID.fromString("db1c371e-9ee9-492c-9b9c-1112a522e4a0")))
                .thenReturn(Optional.of(ProfileType.USER));

        // Run the test
        assertThatThrownBy(() -> profileAdminAuthServiceUnderTest.failIfUnauthorizedToPatch(
                UUID.fromString("db1c371e-9ee9-492c-9b9c-1112a522e4a0"))).isInstanceOf(AccessDeniedException.class);
    }
}
