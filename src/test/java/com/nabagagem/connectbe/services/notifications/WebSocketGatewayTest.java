package com.nabagagem.connectbe.services.notifications;

import com.nabagagem.connectbe.domain.messages.ThreadMessage;
import com.nabagagem.connectbe.domain.messages.ThreadMessageReaction;
import com.nabagagem.connectbe.domain.notification.NotificationCommand;
import com.nabagagem.connectbe.entities.ConnectProfile;
import com.nabagagem.connectbe.entities.Message;
import com.nabagagem.connectbe.entities.MessageType;
import com.nabagagem.connectbe.services.mappers.MessageMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WebSocketGatewayTest {

    @Mock
    private SimpMessagingTemplate mockSimpMessagingTemplate;
    @Mock
    private MessageMapper mockMessageMapper;

    private WebSocketGateway webSocketGatewayUnderTest;

    @BeforeEach
    void setUp() {
        webSocketGatewayUnderTest = new WebSocketGateway(mockSimpMessagingTemplate, mockMessageMapper);
    }

    @Test
    void testSend() throws Exception {
        // Setup
        Message message = Message.builder().build();
        final NotificationCommand notificationCommand = new NotificationCommand(ConnectProfile.builder()
                .id(UUID.fromString("78389fbd-b324-470f-a2f7-9707a5e2b162"))
                .build(), "title", "targetObjectId", Action.CREATED, message);

        // Configure MessageMapper.toDto(...).
        final ThreadMessage threadMessage = new ThreadMessage(UUID.fromString("2728c2d7-715e-43d4-8773-3c2df0749cde"),
                UUID.fromString("4edeeac9-29ec-4855-a979-b067cdc8db31"), "message", "sentBy",
                new URL("https://example.com/"), new MediaType("type", "subtype", StandardCharsets.UTF_8),
                "mediaOriginalName", ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC), false,
                Set.of(new ThreadMessageReaction(UUID.fromString("ca543e8e-2730-4528-af56-574f9d1ba28c"), "reaction",
                        "createdBy")), MessageType.TEXT, false);
        when(mockMessageMapper.toDto(message)).thenReturn(threadMessage);

        // Run the test
        webSocketGatewayUnderTest.send(notificationCommand, Locale.US);

        // Verify the results
        verify(mockSimpMessagingTemplate).convertAndSend("/topics/user/78389fbd-b324-470f-a2f7-9707a5e2b162",
                new WsMessage(threadMessage, Action.CREATED));
    }

    @Test
    void testSend_SimpMessagingTemplateThrowsMessagingException() throws Exception {
        // Setup
        Message message = Message.builder().build();
        final NotificationCommand notificationCommand = new NotificationCommand(ConnectProfile.builder()
                .id(UUID.fromString("78389fbd-b324-470f-a2f7-9707a5e2b162"))
                .build(), "title", "targetObjectId", Action.CREATED, message);

        // Configure MessageMapper.toDto(...).
        final ThreadMessage threadMessage = new ThreadMessage(UUID.fromString("2728c2d7-715e-43d4-8773-3c2df0749cde"),
                UUID.fromString("4edeeac9-29ec-4855-a979-b067cdc8db31"), "message", "sentBy",
                new URL("https://example.com/"), new MediaType("type", "subtype", StandardCharsets.UTF_8),
                "mediaOriginalName", ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC), false,
                Set.of(new ThreadMessageReaction(UUID.fromString("ca543e8e-2730-4528-af56-574f9d1ba28c"), "reaction",
                        "createdBy")), MessageType.TEXT, false);
        when(mockMessageMapper.toDto(message)).thenReturn(threadMessage);

        doThrow(MessagingException.class).when(mockSimpMessagingTemplate).convertAndSend("/topics/user/78389fbd-b324-470f-a2f7-9707a5e2b162",
                new WsMessage(threadMessage, Action.CREATED));

        // Run the test
        assertThatThrownBy(() -> webSocketGatewayUnderTest.send(notificationCommand, Locale.US))
                .isInstanceOf(MessagingException.class);
    }
}
