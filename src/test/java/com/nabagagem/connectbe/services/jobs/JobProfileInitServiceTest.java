package com.nabagagem.connectbe.services.jobs;

import com.nabagagem.connectbe.controllers.LoginHelper;
import com.nabagagem.connectbe.repos.JobRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JobProfileInitServiceTest {

    @Mock
    private JobRepo mockJobRepo;
    @Mock
    private LoginHelper mockLoginHelper;

    private JobAuthService jobAuthServiceUnderTest;

    @BeforeEach
    void setUp() {
        jobAuthServiceUnderTest = new JobAuthService(mockJobRepo, mockLoginHelper);
    }

    @Test
    void testFailIfUnauthorized() {
        // Setup
        // Configure LoginHelper.loggedUser(...).
        UUID loggedUserId = UUID.fromString("85ae9d72-0ed6-418e-a4d6-11607af71417");
        final Optional<UUID> uuid = Optional.of(loggedUserId);
        when(mockLoginHelper.loggedUser()).thenReturn(uuid);

        when(mockJobRepo.existsByOwnerIdAndId(loggedUserId,
                UUID.fromString("3fc87004-edf2-475e-9d6a-487dab6d2d5c"))).thenReturn(true);

        // Run the test
        jobAuthServiceUnderTest.failIfUnauthorized(UUID.fromString("3fc87004-edf2-475e-9d6a-487dab6d2d5c"));

        // Verify the results
    }

    @Test
    void testFailIfUnauthorized_LoginHelperReturnsAbsent() {
        // Setup
        when(mockLoginHelper.loggedUser()).thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(() -> jobAuthServiceUnderTest.failIfUnauthorized(
                UUID.fromString("3fc87004-edf2-475e-9d6a-487dab6d2d5c"))).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void testFailIfUnauthorized_JobRepoReturnsFalse() {
        // Setup
        // Configure LoginHelper.loggedUser(...).
        UUID loggedUserId = UUID.fromString("85ae9d72-0ed6-418e-a4d6-11607af71417");
        final Optional<UUID> uuid = Optional.of(loggedUserId);
        when(mockLoginHelper.loggedUser()).thenReturn(uuid);

        when(mockJobRepo.existsByOwnerIdAndId(loggedUserId,
                UUID.fromString("3fc87004-edf2-475e-9d6a-487dab6d2d5c"))).thenReturn(false);

        // Run the test
        assertThatThrownBy(() -> jobAuthServiceUnderTest.failIfUnauthorized(
                UUID.fromString("3fc87004-edf2-475e-9d6a-487dab6d2d5c"))).isInstanceOf(AccessDeniedException.class);
    }
}
