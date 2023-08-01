package com.nabagagem.connectbe.services.notifications;

import com.nabagagem.connectbe.domain.notification.NotificationCommand;
import com.nabagagem.connectbe.domain.notification.NotificationStatusPayload;
import com.nabagagem.connectbe.domain.notification.UpdateNotifCommand;
import com.nabagagem.connectbe.entities.ConnectProfile;
import com.nabagagem.connectbe.entities.Notification;
import com.nabagagem.connectbe.entities.NotificationType;
import com.nabagagem.connectbe.repos.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationMapper mockNotificationMapper;
    @Mock
    private NotificationRepository mockNotificationRepository;
    @Mock
    private ThreadPoolTaskExecutor mockThreadPoolTaskExecutor;

    private NotificationService notificationServiceUnderTest;

    @BeforeEach
    void setUp() {
        notificationServiceUnderTest = new NotificationService(mockNotificationMapper, mockNotificationRepository,
                List.of((notificationCommand, locale) -> {

                }), mockThreadPoolTaskExecutor);
    }

    @Test
    void testCreate() {
        // Setup
        final NotificationCommand notificationCommand = new NotificationCommand(ConnectProfile.builder()
                .id(UUID.fromString("b79df0ef-2f14-461a-9ee0-49d9cf346088"))
                .build(), "title", "targetObjectId", NotificationType.NEW_MESSAGE, "payload");
        when(mockNotificationMapper.toEntity(new NotificationCommand(ConnectProfile.builder()
                .id(UUID.fromString("b79df0ef-2f14-461a-9ee0-49d9cf346088"))
                .build(), "title", "targetObjectId", NotificationType.NEW_MESSAGE, "payload")))
                .thenReturn(Notification.builder().build());

        // Run the test
        notificationServiceUnderTest.create(notificationCommand);

        // Verify the results
        verify(mockNotificationRepository).save(Notification.builder().build());
        verify(mockThreadPoolTaskExecutor).submit(any(Runnable.class));
    }

    @Test
    void testCreate_NotificationRepositoryThrowsOptimisticLockingFailureException() {
        // Setup
        final NotificationCommand notificationCommand = new NotificationCommand(ConnectProfile.builder()
                .id(UUID.fromString("b79df0ef-2f14-461a-9ee0-49d9cf346088"))
                .build(), "title", "targetObjectId", NotificationType.NEW_MESSAGE, "payload");
        when(mockNotificationMapper.toEntity(new NotificationCommand(ConnectProfile.builder()
                .id(UUID.fromString("b79df0ef-2f14-461a-9ee0-49d9cf346088"))
                .build(), "title", "targetObjectId", NotificationType.NEW_MESSAGE, "payload")))
                .thenReturn(Notification.builder().build());
        when(mockNotificationRepository.save(Notification.builder().build()))
                .thenThrow(OptimisticLockingFailureException.class);

        // Run the test
        assertThatThrownBy(() -> notificationServiceUnderTest.create(notificationCommand))
                .isInstanceOf(OptimisticLockingFailureException.class);
    }

    @Test
    void testCreate_ThreadPoolTaskExecutorThrowsTaskRejectedException() {
        // Setup
        final NotificationCommand notificationCommand = new NotificationCommand(ConnectProfile.builder()
                .id(UUID.fromString("b79df0ef-2f14-461a-9ee0-49d9cf346088"))
                .build(), "title", "targetObjectId", NotificationType.NEW_MESSAGE, "payload");
        when(mockNotificationMapper.toEntity(new NotificationCommand(ConnectProfile.builder()
                .id(UUID.fromString("b79df0ef-2f14-461a-9ee0-49d9cf346088"))
                .build(), "title", "targetObjectId", NotificationType.NEW_MESSAGE, "payload")))
                .thenReturn(Notification.builder().build());
        doThrow(TaskRejectedException.class).when(mockThreadPoolTaskExecutor).submit(any(Runnable.class));
        // Run the test
        assertThatThrownBy(() -> notificationServiceUnderTest.create(notificationCommand))
                .isInstanceOf(TaskRejectedException.class);
        verify(mockNotificationRepository).save(Notification.builder().build());
    }

    @Test
    void testList() {
        // Setup
        final List<Notification> expectedResult = List.of(Notification.builder().build());
        when(mockNotificationRepository.findByTargetProfileId(
                UUID.fromString("1764220f-02a9-4eb3-bd91-cb980dccd6c8")))
                .thenReturn(List.of(Notification.builder().build()));

        // Run the test
        final List<Notification> result = notificationServiceUnderTest.list(
                UUID.fromString("1764220f-02a9-4eb3-bd91-cb980dccd6c8"));

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testList_NotificationRepositoryReturnsNoItems() {
        // Setup
        when(mockNotificationRepository.findByTargetProfileId(
                UUID.fromString("1764220f-02a9-4eb3-bd91-cb980dccd6c8"))).thenReturn(Collections.emptyList());

        // Run the test
        final List<Notification> result = notificationServiceUnderTest.list(
                UUID.fromString("1764220f-02a9-4eb3-bd91-cb980dccd6c8"));

        // Verify the results
        assertThat(result).isEqualTo(Collections.emptyList());
    }

    @Test
    void testUpdate() {
        // Setup
        UUID notificationId = UUID.fromString("c0574492-380d-4368-8efe-067798d1127a");
        final UpdateNotifCommand updateNotifCommand = new UpdateNotifCommand(
                notificationId, new NotificationStatusPayload(false));

        // Run the test
        notificationServiceUnderTest.update(updateNotifCommand);

        // Verify the results
        verify(mockNotificationRepository).update(notificationId, false);
    }
}
