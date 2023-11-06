package com.nabagagem.connectbe.services.profile;

import com.nabagagem.connectbe.entities.Notification;
import com.nabagagem.connectbe.repos.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.OptimisticLockingFailureException;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository mockNotificationRepository;

    private NotificationService notificationServiceUnderTest;

    @BeforeEach
    void setUp() throws Exception {
        notificationServiceUnderTest = new NotificationService(mockNotificationRepository);
    }

    @Test
    void testDeleteForUser() {
        // Setup
        Notification notification = Notification.builder()
                .id(UUID.randomUUID())
                .build();
        when(mockNotificationRepository.findByTargetProfileId(
                UUID.fromString("bfdf5ca9-c369-4205-9cf4-edd62db9d9d5")))
                .thenReturn(List.of(notification));

        // Run the test
        notificationServiceUnderTest.deleteForUser(UUID.fromString("bfdf5ca9-c369-4205-9cf4-edd62db9d9d5"));

        // Verify the results
        verify(mockNotificationRepository).deleteAll(List.of(notification));
    }

    @Test
    void testDeleteForUser_NotificationRepositoryDeleteAllThrowsOptimisticLockingFailureException() {
        // Setup
        when(mockNotificationRepository.findByTargetProfileId(
                UUID.fromString("bfdf5ca9-c369-4205-9cf4-edd62db9d9d5")))
                .thenReturn(List.of(Notification.builder().build()));
        doThrow(OptimisticLockingFailureException.class).when(mockNotificationRepository).deleteAll(
                List.of(Notification.builder().build()));

        // Run the test
        assertThatThrownBy(() -> notificationServiceUnderTest.deleteForUser(
                UUID.fromString("bfdf5ca9-c369-4205-9cf4-edd62db9d9d5")))
                .isInstanceOf(OptimisticLockingFailureException.class);
    }
}
