package com.nabagagem.connectbe.services.notifications;

import com.nabagagem.connectbe.domain.notification.EventNotification;
import com.nabagagem.connectbe.domain.notification.NotificationCommand;
import com.nabagagem.connectbe.entities.Audit;
import com.nabagagem.connectbe.entities.ConnectProfile;
import com.nabagagem.connectbe.entities.Message;
import com.nabagagem.connectbe.entities.Thread;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageNotificationListenerTest {

    @Mock
    private MessageNotificationService mockMessageNotificationService;

    private MessageNotificationListener messageNotificationListenerUnderTest;

    @BeforeEach
    void setUp() {
        messageNotificationListenerUnderTest = new MessageNotificationListener(mockMessageNotificationService);
    }

    @Test
    void testAfterCommit1() {
        // Setup
        PublishNotification publishNotification = mock(PublishNotification.class);
        when(publishNotification.value()).thenReturn(Action.CREATED);

        UUID recipientId = UUID.randomUUID();
        UUID senderId = UUID.randomUUID();
        UUID threadId = UUID.randomUUID();
        Audit audit = new Audit();
        audit.setModifiedBy(senderId.toString());
        Message message = Message.builder()
                .id(UUID.randomUUID())
                .text("some-text")
                .thread(
                        Thread.builder()
                                .id(threadId)
                                .recipient(ConnectProfile.builder()
                                        .id(recipientId)
                                        .build())
                                .sender(
                                        ConnectProfile.builder()
                                                .id(senderId)
                                                .build())
                                .build()
                )
                .audit(audit)
                .build();
        final EventNotification notification = new EventNotification(publishNotification, message);

        // Run the test
        messageNotificationListenerUnderTest.afterCommit(notification);

        // Verify the results
        verify(mockMessageNotificationService).create(new NotificationCommand(ConnectProfile.builder()
                .id(recipientId)
                .build(), "some-text", threadId.toString(), Action.CREATED, message));
    }

    @Test
    void testAfterCommit3() {
        // Setup
        Message lastMessage = Message.builder()
                .text("text")
                .audit(new Audit())
                .build();
        final Thread thread = Thread.builder()
                .id(UUID.fromString("6c13758d-1d51-4b45-9494-3ac38a9259d2"))
                .sender(ConnectProfile.builder()
                        .id(UUID.fromString("33b7d3d8-89a6-46fb-a5c9-e76295906ce3"))
                        .build())
                .recipient(ConnectProfile.builder()
                        .id(UUID.fromString("33b7d3d8-89a6-46fb-a5c9-e76295906ce3"))
                        .build())
                .lastMessage(lastMessage)
                .build();
        lastMessage.setThread(thread);

        // Run the test
        messageNotificationListenerUnderTest.afterCommit(thread, Action.CREATED);

        // Verify the results
        verify(mockMessageNotificationService).create(new NotificationCommand(ConnectProfile.builder()
                .id(UUID.fromString("33b7d3d8-89a6-46fb-a5c9-e76295906ce3"))
                .build(), "text", "6c13758d-1d51-4b45-9494-3ac38a9259d2", Action.CREATED, lastMessage));
    }
}
