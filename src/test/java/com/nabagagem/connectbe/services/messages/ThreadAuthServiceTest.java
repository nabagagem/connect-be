package com.nabagagem.connectbe.services.messages;

import com.nabagagem.connectbe.controllers.LoginHelper;
import com.nabagagem.connectbe.domain.messages.PatchThreadPayload;
import com.nabagagem.connectbe.entities.Audit;
import com.nabagagem.connectbe.entities.Thread;
import com.nabagagem.connectbe.entities.ThreadStatus;
import com.nabagagem.connectbe.repos.ThreadRepo;
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
class ThreadAuthServiceTest {

    @Mock
    private ThreadRepo mockThreadRepo;
    @Mock
    private LoginHelper mockLoginHelper;

    private ThreadAuthService threadAuthServiceUnderTest;

    @BeforeEach
    void setUp() {
        threadAuthServiceUnderTest = new ThreadAuthService(mockThreadRepo, mockLoginHelper);
    }

    @Test
    void testFailIfUnableToDelete() {
        // Setup
        // Configure LoginHelper.loggedUser(...).
        UUID loggedUserId = UUID.fromString("d82de3b7-3074-4950-8cee-fd775b53090c");
        final Optional<UUID> uuid = Optional.of(loggedUserId);
        when(mockLoginHelper.loggedUser()).thenReturn(uuid);

        when(mockThreadRepo.existsByIdAndUsers(UUID.fromString("0ded939c-a84c-46c7-ad37-d9be985907e0"),
                loggedUserId)).thenReturn(true);

        // Run the test
        threadAuthServiceUnderTest.failIfUnableToDelete(UUID.fromString("0ded939c-a84c-46c7-ad37-d9be985907e0"));

        // Verify the results
    }

    @Test
    void testFailIfUnableToDelete_LoginHelperReturnsAbsent() {
        // Setup
        when(mockLoginHelper.loggedUser()).thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(() -> threadAuthServiceUnderTest.failIfUnableToDelete(
                UUID.fromString("0ded939c-a84c-46c7-ad37-d9be985907e0"))).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void testFailIfUnableToDelete_ThreadRepoReturnsFalse() {
        // Setup
        // Configure LoginHelper.loggedUser(...).
        UUID loggedUserId = UUID.fromString("1de5868e-5385-4c1e-889e-5f31cf785156");
        final Optional<UUID> uuid = Optional.of(loggedUserId);
        when(mockLoginHelper.loggedUser()).thenReturn(uuid);

        when(mockThreadRepo.existsByIdAndUsers(UUID.fromString("0ded939c-a84c-46c7-ad37-d9be985907e0"),
                loggedUserId)).thenReturn(false);

        // Run the test
        assertThatThrownBy(() -> threadAuthServiceUnderTest.failIfUnableToDelete(
                UUID.fromString("0ded939c-a84c-46c7-ad37-d9be985907e0"))).isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void testFailIfUnableToRead() {
        // Setup
        // Configure LoginHelper.loggedUser(...).
        UUID loggedUserId = UUID.fromString("1de5868e-5385-4c1e-889e-5f31cf785156");
        final Optional<UUID> uuid = Optional.of(loggedUserId);
        when(mockLoginHelper.loggedUser()).thenReturn(uuid);

        when(mockThreadRepo.existsByIdAndUsers(UUID.fromString("0ded939c-a84c-46c7-ad37-d9be985907e0"),
                loggedUserId)).thenReturn(true);

        // Run the test
        threadAuthServiceUnderTest.failIfUnableToRead(UUID.fromString("0ded939c-a84c-46c7-ad37-d9be985907e0"));

        // Verify the results
    }

    @Test
    void testFailIfUnableToRead_LoginHelperReturnsAbsent() {
        // Setup
        when(mockLoginHelper.loggedUser()).thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(() -> threadAuthServiceUnderTest.failIfUnableToRead(
                UUID.fromString("0ded939c-a84c-46c7-ad37-d9be985907e0"))).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void testFailIfUnableToRead_ThreadRepoReturnsFalse() {
        // Setup
        // Configure LoginHelper.loggedUser(...).
        UUID profileId = UUID.fromString("34ada13d-e7a0-4232-b73e-997e3ec250f0");
        final Optional<UUID> uuid = Optional.of(profileId);
        when(mockLoginHelper.loggedUser()).thenReturn(uuid);

        when(mockThreadRepo.existsByIdAndUsers(UUID.fromString("0ded939c-a84c-46c7-ad37-d9be985907e0"),
                profileId)).thenReturn(false);

        // Run the test
        assertThatThrownBy(() -> threadAuthServiceUnderTest.failIfUnableToRead(
                UUID.fromString("0ded939c-a84c-46c7-ad37-d9be985907e0"))).isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void testFailIfUnableToUpdate() {
        // Setup
        final PatchThreadPayload patchThreadPayload = new PatchThreadPayload(ThreadStatus.OPEN);

        // Configure ThreadRepo.findById(...).
        final Optional<Thread> thread = Optional.of(Thread.builder()
                .status(ThreadStatus.OPEN)
                .audit(new Audit())
                .build());
        when(mockThreadRepo.findById(UUID.fromString("b0f3607e-5e9a-4b68-a766-4e3d48a7f6f2"))).thenReturn(thread);

        // Configure LoginHelper.loggedUser(...).
        final Optional<UUID> uuid = Optional.of(UUID.fromString("54d17bcd-ef1f-49a4-a4be-188cf62884f8"));
        when(mockLoginHelper.loggedUser()).thenReturn(uuid);

        // Run the test
        threadAuthServiceUnderTest.failIfUnableToUpdate(UUID.fromString("b0f3607e-5e9a-4b68-a766-4e3d48a7f6f2"),
                patchThreadPayload);

        // Verify the results
    }

    @Test
    void testFailIfUnableToUpdate_ThreadRepoReturnsAbsent() {
        // Setup
        final PatchThreadPayload patchThreadPayload = new PatchThreadPayload(ThreadStatus.OPEN);
        when(mockThreadRepo.findById(UUID.fromString("b0f3607e-5e9a-4b68-a766-4e3d48a7f6f2")))
                .thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(() -> threadAuthServiceUnderTest.failIfUnableToUpdate(
                UUID.fromString("b0f3607e-5e9a-4b68-a766-4e3d48a7f6f2"), patchThreadPayload))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void testFailIfUnableToUpdate_LoginHelperReturnsAbsent() {
        // Setup
        final PatchThreadPayload patchThreadPayload = new PatchThreadPayload(ThreadStatus.OPEN);

        // Configure ThreadRepo.findById(...).
        final Optional<Thread> thread = Optional.of(Thread.builder()
                .status(ThreadStatus.OPEN)
                .audit(new Audit())
                .build());
        when(mockThreadRepo.findById(UUID.fromString("b0f3607e-5e9a-4b68-a766-4e3d48a7f6f2"))).thenReturn(thread);

        when(mockLoginHelper.loggedUser()).thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(() -> threadAuthServiceUnderTest.failIfUnableToUpdate(
                UUID.fromString("b0f3607e-5e9a-4b68-a766-4e3d48a7f6f2"), patchThreadPayload))
                .isInstanceOf(NoSuchElementException.class);
    }
}
