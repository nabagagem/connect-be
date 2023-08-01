package com.nabagagem.connectbe.services.messages;

import com.nabagagem.connectbe.controllers.LoginHelper;
import com.nabagagem.connectbe.repos.ReactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReactProfileInitServiceTest {

    @Mock
    private ReactionRepository mockReactionRepository;
    @Mock
    private LoginHelper mockLoginHelper;

    private ReactAuthService reactAuthServiceUnderTest;

    @BeforeEach
    void setUp() {
        reactAuthServiceUnderTest = new ReactAuthService(mockReactionRepository, mockLoginHelper);
    }

    @Test
    void testFailIfUnableToDelete() {
        // Setup
        // Configure LoginHelper.getLoggedUserId(...).
        final UUID uuid = UUID.fromString("436d9f90-8734-430b-93c1-bffd0bada7b8");
        when(mockLoginHelper.getLoggedUserId()).thenReturn(uuid);

        when(mockReactionRepository.existsByIdAndOwner(UUID.fromString("abb69f63-3723-4aac-bb60-55845641cf2f"),
                "436d9f90-8734-430b-93c1-bffd0bada7b8")).thenReturn(true);

        // Run the test
        reactAuthServiceUnderTest.failIfUnableToDelete(UUID.fromString("abb69f63-3723-4aac-bb60-55845641cf2f"));

        // Verify the results
    }

    @Test
    void testFailIfUnableToDelete_ReactionRepositoryReturnsFalse() {
        // Setup
        // Configure LoginHelper.getLoggedUserId(...).
        final UUID uuid = UUID.fromString("436d9f90-8734-430b-93c1-bffd0bada7b8");
        when(mockLoginHelper.getLoggedUserId()).thenReturn(uuid);

        when(mockReactionRepository.existsByIdAndOwner(UUID.fromString("abb69f63-3723-4aac-bb60-55845641cf2f"),
                "436d9f90-8734-430b-93c1-bffd0bada7b8")).thenReturn(false);

        // Run the test
        assertThatThrownBy(() -> reactAuthServiceUnderTest.failIfUnableToDelete(
                UUID.fromString("abb69f63-3723-4aac-bb60-55845641cf2f"))).isInstanceOf(AccessDeniedException.class);
    }
}
