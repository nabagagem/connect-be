package com.nabagagem.connectbe.services.messages;

import com.nabagagem.connectbe.controllers.LoginHelper;
import com.nabagagem.connectbe.domain.exceptions.MessageCannotBeRead;
import com.nabagagem.connectbe.domain.messages.MessagePatchPayload;
import com.nabagagem.connectbe.repos.MessageRepo;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MessageAuthServiceTest {

    @Mock
    private MessageRepo mockMessageRepo;
    @Mock
    private ThreadAuthService mockThreadAuthService;
    @Mock
    private LoginHelper mockLoginHelper;

    private MessageAuthService messageAuthServiceUnderTest;

    @BeforeEach
    void setUp() {
        messageAuthServiceUnderTest = new MessageAuthService(mockMessageRepo, mockThreadAuthService, mockLoginHelper);
    }

    @Test
    void testFailIfUnableToWriteOnThread() {
        // Setup
        // Run the test
        messageAuthServiceUnderTest.failIfUnableToWriteOnThread(
                UUID.fromString("438a955f-17ea-4df5-85f4-4f21bd2753cc"));

        // Verify the results
        verify(mockThreadAuthService).failIfUnableToRead(UUID.fromString("438a955f-17ea-4df5-85f4-4f21bd2753cc"));
    }

    @Test
    void testFailIfUnableToDelete() {
        // Setup
        // Configure LoginHelper.loggedUser(...).
        final Optional<UUID> uuid = Optional.of(UUID.fromString("f38524ca-6e5e-4754-ac52-d914fd0d05fc"));
        when(mockLoginHelper.loggedUser()).thenReturn(uuid);

        when(mockMessageRepo.existsByIdAndCreator(UUID.fromString("72e76788-e50e-40af-aa1e-2e4192f9d2b5"),
                uuid.get().toString())).thenReturn(true);

        // Run the test
        messageAuthServiceUnderTest.failIfUnableToDelete(UUID.fromString("72e76788-e50e-40af-aa1e-2e4192f9d2b5"));

        // Verify the results
    }

    @Test
    void testFailIfUnableToDelete_LoginHelperReturnsAbsent() {
        // Setup
        when(mockLoginHelper.loggedUser()).thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(() -> messageAuthServiceUnderTest.failIfUnableToDelete(
                UUID.fromString("72e76788-e50e-40af-aa1e-2e4192f9d2b5"))).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void testFailIfUnableToDelete_MessageRepoReturnsFalse() {
        // Setup
        // Configure LoginHelper.loggedUser(...).
        final Optional<UUID> uuid = Optional.of(UUID.fromString("f38524ca-6e5e-4754-ac52-d914fd0d05fc"));
        when(mockLoginHelper.loggedUser()).thenReturn(uuid);

        when(mockMessageRepo.existsByIdAndCreator(UUID.fromString("72e76788-e50e-40af-aa1e-2e4192f9d2b5"),
                uuid.get().toString())).thenReturn(false);

        // Run the test
        assertThatThrownBy(() -> messageAuthServiceUnderTest.failIfUnableToDelete(
                UUID.fromString("72e76788-e50e-40af-aa1e-2e4192f9d2b5"))).isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void testFailIfUnableToPatch() {
        // Setup
        final MessagePatchPayload messagePatchPayload = new MessagePatchPayload("text", false);

        // Configure LoginHelper.getLoggedUserId(...).
        final UUID uuid = UUID.fromString("b9796fcf-0520-4b04-9d5a-4f73afc1908a");
        when(mockLoginHelper.getLoggedUserId()).thenReturn(uuid);

        when(mockMessageRepo.isTheRecipientOf(UUID.fromString("72e76788-e50e-40af-aa1e-2e4192f9d2b5"),
                UUID.fromString("b9796fcf-0520-4b04-9d5a-4f73afc1908a"),
                "b9796fcf-0520-4b04-9d5a-4f73afc1908a")).thenReturn(true);

        // Configure LoginHelper.loggedUser(...).
        final Optional<UUID> uuid1 = Optional.of(UUID.fromString("b9796fcf-0520-4b04-9d5a-4f73afc1908a"));
        when(mockLoginHelper.loggedUser()).thenReturn(uuid1);

        when(mockMessageRepo.existsByIdAndCreator(UUID.fromString("72e76788-e50e-40af-aa1e-2e4192f9d2b5"),
                uuid1.get().toString())).thenReturn(true);

        // Run the test
        messageAuthServiceUnderTest.failIfUnableToPatch(UUID.fromString("72e76788-e50e-40af-aa1e-2e4192f9d2b5"),
                messagePatchPayload);

        // Verify the results
    }

    @Test
    void testFailIfUnableToPatch_MessageRepoIsTheRecipientOfReturnsFalse() {
        // Setup
        final MessagePatchPayload messagePatchPayload = new MessagePatchPayload("text", false);

        // Configure LoginHelper.getLoggedUserId(...).
        final UUID uuid = UUID.fromString("b9796fcf-0520-4b04-9d5a-4f73afc1908a");
        when(mockLoginHelper.getLoggedUserId()).thenReturn(uuid);

        when(mockMessageRepo.isTheRecipientOf(UUID.fromString("72e76788-e50e-40af-aa1e-2e4192f9d2b5"),
                UUID.fromString("b9796fcf-0520-4b04-9d5a-4f73afc1908a"),
                "b9796fcf-0520-4b04-9d5a-4f73afc1908a")).thenReturn(false);

        // Run the test
        assertThatThrownBy(() -> messageAuthServiceUnderTest.failIfUnableToPatch(
                UUID.fromString("72e76788-e50e-40af-aa1e-2e4192f9d2b5"), messagePatchPayload))
                .isInstanceOf(MessageCannotBeRead.class);
    }

    @Test
    void testFailIfUnableToPatch_LoginHelperLoggedUserReturnsAbsent() {
        // Setup
        final MessagePatchPayload messagePatchPayload = new MessagePatchPayload("text", false);

        // Configure LoginHelper.getLoggedUserId(...).
        final UUID uuid = UUID.fromString("b9796fcf-0520-4b04-9d5a-4f73afc1908a");
        when(mockLoginHelper.getLoggedUserId()).thenReturn(uuid);

        when(mockMessageRepo.isTheRecipientOf(UUID.fromString("72e76788-e50e-40af-aa1e-2e4192f9d2b5"),
                UUID.fromString("b9796fcf-0520-4b04-9d5a-4f73afc1908a"),
                "b9796fcf-0520-4b04-9d5a-4f73afc1908a")).thenReturn(true);
        when(mockLoginHelper.loggedUser()).thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(() -> messageAuthServiceUnderTest.failIfUnableToPatch(
                UUID.fromString("72e76788-e50e-40af-aa1e-2e4192f9d2b5"), messagePatchPayload))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void testFailIfUnableToRead() {
        // Setup
        // Configure LoginHelper.getLoggedUserId(...).
        final UUID uuid = UUID.fromString("cdb4ab42-97d1-4785-98da-6e8eb07bf25a");
        when(mockLoginHelper.getLoggedUserId()).thenReturn(uuid);

        when(mockMessageRepo.isUserOnThread(UUID.fromString("47619a25-f574-439c-9800-40a5f84a2244"),
                UUID.fromString("cdb4ab42-97d1-4785-98da-6e8eb07bf25a"))).thenReturn(true);

        // Run the test
        messageAuthServiceUnderTest.failIfUnableToRead(UUID.fromString("47619a25-f574-439c-9800-40a5f84a2244"));

        // Verify the results
    }

    @Test
    void testFailIfUnableToRead_MessageRepoReturnsFalse() {
        // Setup
        // Configure LoginHelper.getLoggedUserId(...).
        final UUID uuid = UUID.fromString("cdb4ab42-97d1-4785-98da-6e8eb07bf25a");
        when(mockLoginHelper.getLoggedUserId()).thenReturn(uuid);

        when(mockMessageRepo.isUserOnThread(UUID.fromString("47619a25-f574-439c-9800-40a5f84a2244"),
                UUID.fromString("cdb4ab42-97d1-4785-98da-6e8eb07bf25a"))).thenReturn(false);

        // Run the test
        assertThatThrownBy(() -> messageAuthServiceUnderTest.failIfUnableToRead(
                UUID.fromString("47619a25-f574-439c-9800-40a5f84a2244"))).isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void testFailIfUnableToReact() {
        // Setup
        // Configure LoginHelper.getLoggedUserId(...).
        final UUID uuid = UUID.fromString("cdb4ab42-97d1-4785-98da-6e8eb07bf25a");
        when(mockLoginHelper.getLoggedUserId()).thenReturn(uuid);

        when(mockMessageRepo.isUserOnThread(UUID.fromString("47619a25-f574-439c-9800-40a5f84a2244"),
                UUID.fromString("cdb4ab42-97d1-4785-98da-6e8eb07bf25a"))).thenReturn(true);

        // Run the test
        messageAuthServiceUnderTest.failIfUnableToReact(UUID.fromString("47619a25-f574-439c-9800-40a5f84a2244"));

        // Verify the results
    }

    @Test
    void testFailIfUnableToReact_MessageRepoReturnsFalse() {
        // Setup
        // Configure LoginHelper.getLoggedUserId(...).
        final UUID uuid = UUID.fromString("cdb4ab42-97d1-4785-98da-6e8eb07bf25a");
        when(mockLoginHelper.getLoggedUserId()).thenReturn(uuid);

        when(mockMessageRepo.isUserOnThread(UUID.fromString("47619a25-f574-439c-9800-40a5f84a2244"),
                UUID.fromString("cdb4ab42-97d1-4785-98da-6e8eb07bf25a"))).thenReturn(false);

        // Run the test
        assertThatThrownBy(() -> messageAuthServiceUnderTest.failIfUnableToReact(
                UUID.fromString("47619a25-f574-439c-9800-40a5f84a2244"))).isInstanceOf(AccessDeniedException.class);
    }
}
