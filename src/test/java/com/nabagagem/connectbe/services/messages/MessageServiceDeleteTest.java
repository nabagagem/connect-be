package com.nabagagem.connectbe.services.messages;

import com.nabagagem.connectbe.entities.ConnectProfile;
import com.nabagagem.connectbe.entities.Media;
import com.nabagagem.connectbe.entities.Message;
import com.nabagagem.connectbe.entities.Thread;
import com.nabagagem.connectbe.entities.ThreadStatus;
import com.nabagagem.connectbe.repos.BidRepository;
import com.nabagagem.connectbe.repos.MessageRepo;
import com.nabagagem.connectbe.repos.ProfileRepo;
import com.nabagagem.connectbe.repos.ThreadRepo;
import com.nabagagem.connectbe.services.MediaService;
import com.nabagagem.connectbe.services.search.KeywordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MessageServiceDeleteTest {

    @Mock
    private MessageRepo mockMessageRepo;
    @Mock
    private ProfileRepo mockProfileRepo;
    @Mock
    private ThreadRepo mockThreadRepo;
    @Mock
    private BidRepository mockBidRepository;
    @Mock
    private MediaService mockMediaService;
    @Mock
    private KeywordService mockKeywordService;

    private MessageService messageServiceUnderTest;

    @BeforeEach
    void setUp() {
        messageServiceUnderTest = new MessageService(mockMessageRepo, mockProfileRepo, mockThreadRepo,
                mockBidRepository, mockMediaService, mockKeywordService);
    }

    @Test
    void testDelete_NotLastMessage() {
        // Setup
        Thread thread = Thread.builder()
                .id(UUID.fromString("15b950a1-f9eb-4c96-90f0-ace222323352"))
                .sender(ConnectProfile.builder()
                        .id(UUID.fromString("8379ee7d-608f-4981-8ead-d5b6c278491c"))
                        .build())
                .recipient(ConnectProfile.builder()
                        .id(UUID.fromString("8379ee7d-608f-4981-8ead-d5b6c278491c"))
                        .build())
                .lastMessageAt(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC))
                .messages(Set.of())
                .lastMessage(Message.builder().id(UUID.randomUUID()).build())
                .status(ThreadStatus.OPEN)
                .build();

        final Message message2BeDeleted = Message.builder()
                .id(UUID.fromString("22ce91ae-b963-448a-9bd0-46ad7934e570"))
                .thread(thread)
                .text("message")
                .media(Media.builder().build())
                .read(false)
                .keywords(Set.of("value"))
                .textUpdated(false)
                .build();

        // Configure MessageRepo.findWithThread(...).
        when(mockMessageRepo.findWithThread(UUID.fromString("27ab7825-7f13-4e41-8f46-11e859eb6d3b")))
                .thenReturn(Optional.of(message2BeDeleted));


        // Run the test
        final Message result = messageServiceUnderTest.delete(UUID.fromString("27ab7825-7f13-4e41-8f46-11e859eb6d3b"));

        // Verify the results
        assertThat(result).isEqualTo(message2BeDeleted);
        verify(mockMessageRepo).delete(message2BeDeleted);
    }

    @Test
    void testDelete_LastMessageWithPrevious() {
        // Setup
        ArgumentCaptor<Thread> threadArgumentCaptor = ArgumentCaptor.forClass(Thread.class);
        UUID messageId = UUID.fromString("22ce91ae-b963-448a-9bd0-46ad7934e570");
        UUID threadId = UUID.fromString("15b950a1-f9eb-4c96-90f0-ace222323352");
        Thread thread = Thread.builder()
                .id(threadId)
                .sender(ConnectProfile.builder()
                        .id(UUID.fromString("8379ee7d-608f-4981-8ead-d5b6c278491c"))
                        .build())
                .recipient(ConnectProfile.builder()
                        .id(UUID.fromString("8379ee7d-608f-4981-8ead-d5b6c278491c"))
                        .build())
                .lastMessageAt(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC))
                .messages(Set.of())
                .lastMessage(Message.builder().id(messageId).build())
                .status(ThreadStatus.OPEN)
                .build();

        final Message message2BeDeleted = Message.builder()
                .id(messageId)
                .thread(thread)
                .text("message")
                .media(Media.builder().build())
                .read(false)
                .keywords(Set.of("value"))
                .textUpdated(false)
                .build();

        // Configure MessageRepo.findWithThread(...).
        when(mockMessageRepo.findWithThread(messageId))
                .thenReturn(Optional.of(message2BeDeleted));

        Message newLastMessage = Message.builder().id(UUID.randomUUID()).build();
        when(mockMessageRepo.findPreviousOf(threadId, messageId))
                .thenReturn(Optional.ofNullable(newLastMessage));


        // Run the test
        final Message result = messageServiceUnderTest.delete(messageId);

        // Verify the results
        assertThat(result).isEqualTo(message2BeDeleted);
        verify(mockMessageRepo).delete(message2BeDeleted);
        verify(mockThreadRepo).save(threadArgumentCaptor.capture());
        assertThat(threadArgumentCaptor.getValue().getLastMessage())
                .isEqualTo(newLastMessage);
    }

    @Test
    void testDelete_LastMessageWithoutPrevious() {
        // Setup
        UUID messageId = UUID.fromString("22ce91ae-b963-448a-9bd0-46ad7934e570");
        UUID threadId = UUID.fromString("15b950a1-f9eb-4c96-90f0-ace222323352");
        Thread thread = Thread.builder()
                .id(threadId)
                .sender(ConnectProfile.builder()
                        .id(UUID.fromString("8379ee7d-608f-4981-8ead-d5b6c278491c"))
                        .build())
                .recipient(ConnectProfile.builder()
                        .id(UUID.fromString("8379ee7d-608f-4981-8ead-d5b6c278491c"))
                        .build())
                .lastMessageAt(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC))
                .messages(Set.of())
                .lastMessage(Message.builder().id(messageId).build())
                .status(ThreadStatus.OPEN)
                .build();

        final Message message2BeDeleted = Message.builder()
                .id(messageId)
                .thread(thread)
                .text("message")
                .media(Media.builder().build())
                .read(false)
                .keywords(Set.of("value"))
                .textUpdated(false)
                .build();

        // Configure MessageRepo.findWithThread(...).
        when(mockMessageRepo.findWithThread(messageId))
                .thenReturn(Optional.of(message2BeDeleted));

        when(mockMessageRepo.findPreviousOf(threadId, messageId))
                .thenReturn(Optional.empty());


        // Run the test
        final Message result = messageServiceUnderTest.delete(messageId);

        // Verify the results
        assertThat(result).isEqualTo(message2BeDeleted);
        verify(mockThreadRepo).delete(thread);
    }
}
