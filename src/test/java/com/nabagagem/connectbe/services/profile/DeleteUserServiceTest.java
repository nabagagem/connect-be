package com.nabagagem.connectbe.services.profile;

import com.nabagagem.connectbe.entities.ConnectProfile;
import com.nabagagem.connectbe.repos.ProfileRepo;
import com.nabagagem.connectbe.repos.UserMailNotificationRepository;
import com.nabagagem.connectbe.services.jobs.JobService;
import com.nabagagem.connectbe.services.messages.MessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.OptimisticLockingFailureException;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteUserServiceTest {

    @Mock
    private ProfileRepo mockProfileRepo;
    @Mock
    private JobService mockJobService;
    @Mock
    private MessageService mockMessageService;
    @Mock
    private NotificationService mockNotificationService;
    @Mock
    private UserMailNotificationRepository mockUserMailNotificationRepository;

    private DeleteUserService deleteUserServiceUnderTest;

    @BeforeEach
    void setUp() throws Exception {
        deleteUserServiceUnderTest = new DeleteUserService(mockProfileRepo, mockJobService, mockMessageService,
                mockNotificationService, mockUserMailNotificationRepository);
    }

    @Test
    void testDelete() {
        // Setup
        // Configure ProfileRepo.findById(...).
        final Optional<ConnectProfile> connectProfile = Optional.of(ConnectProfile.builder().build());
        when(mockProfileRepo.findById(UUID.fromString("bf1cb5e1-e425-482e-a9e2-342ffe323949")))
                .thenReturn(connectProfile);

        // Run the test
        deleteUserServiceUnderTest.delete(UUID.fromString("bf1cb5e1-e425-482e-a9e2-342ffe323949"));

        // Verify the results
        verify(mockNotificationService).deleteForUser(UUID.fromString("bf1cb5e1-e425-482e-a9e2-342ffe323949"));
        verify(mockUserMailNotificationRepository).deleteByProfileId(
                UUID.fromString("bf1cb5e1-e425-482e-a9e2-342ffe323949"));
        verify(mockMessageService).deleteForUser(UUID.fromString("bf1cb5e1-e425-482e-a9e2-342ffe323949"));
        verify(mockJobService).deleteForUser(UUID.fromString("bf1cb5e1-e425-482e-a9e2-342ffe323949"));
        verify(mockProfileRepo).delete(ConnectProfile.builder().build());
    }

    @Test
    void testDelete_ProfileRepoFindByIdReturnsAbsent() {
        // Setup
        when(mockProfileRepo.findById(UUID.fromString("bf1cb5e1-e425-482e-a9e2-342ffe323949")))
                .thenReturn(Optional.empty());

        // Run the test
        deleteUserServiceUnderTest.delete(UUID.fromString("bf1cb5e1-e425-482e-a9e2-342ffe323949"));

        // Verify the results
        verify(mockNotificationService).deleteForUser(UUID.fromString("bf1cb5e1-e425-482e-a9e2-342ffe323949"));
        verify(mockUserMailNotificationRepository).deleteByProfileId(
                UUID.fromString("bf1cb5e1-e425-482e-a9e2-342ffe323949"));
        verify(mockMessageService).deleteForUser(UUID.fromString("bf1cb5e1-e425-482e-a9e2-342ffe323949"));
        verify(mockJobService).deleteForUser(UUID.fromString("bf1cb5e1-e425-482e-a9e2-342ffe323949"));
    }

    @Test
    void testDelete_ProfileRepoDeleteThrowsOptimisticLockingFailureException() {
        // Setup
        // Configure ProfileRepo.findById(...).
        final Optional<ConnectProfile> connectProfile = Optional.of(ConnectProfile.builder().build());
        when(mockProfileRepo.findById(UUID.fromString("bf1cb5e1-e425-482e-a9e2-342ffe323949")))
                .thenReturn(connectProfile);

        doThrow(OptimisticLockingFailureException.class).when(mockProfileRepo).delete(ConnectProfile.builder().build());

        // Run the test
        assertThatThrownBy(() -> deleteUserServiceUnderTest.delete(
                UUID.fromString("bf1cb5e1-e425-482e-a9e2-342ffe323949")))
                .isInstanceOf(OptimisticLockingFailureException.class);
        verify(mockNotificationService).deleteForUser(UUID.fromString("bf1cb5e1-e425-482e-a9e2-342ffe323949"));
        verify(mockUserMailNotificationRepository).deleteByProfileId(
                UUID.fromString("bf1cb5e1-e425-482e-a9e2-342ffe323949"));
        verify(mockMessageService).deleteForUser(UUID.fromString("bf1cb5e1-e425-482e-a9e2-342ffe323949"));
        verify(mockJobService).deleteForUser(UUID.fromString("bf1cb5e1-e425-482e-a9e2-342ffe323949"));
    }
}
