package com.nabagagem.connectbe.services.messages;

import com.nabagagem.connectbe.domain.messages.MessagePatchPayload;
import com.nabagagem.connectbe.domain.messages.PatchThreadPayload;
import com.nabagagem.connectbe.domain.messages.SendMessageCommand;
import com.nabagagem.connectbe.domain.messages.SendMessagePayload;
import com.nabagagem.connectbe.domain.messages.TextPayload;
import com.nabagagem.connectbe.domain.messages.ThreadMessageCommand;
import com.nabagagem.connectbe.entities.Bid;
import com.nabagagem.connectbe.entities.ConnectProfile;
import com.nabagagem.connectbe.entities.Media;
import com.nabagagem.connectbe.entities.Message;
import com.nabagagem.connectbe.entities.ProfileThreadItem;
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
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.OptimisticLockingFailureException;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

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
    void testSend() {
        // Setup
        UUID bidId = UUID.fromString("eb9a0e47-0cf0-4c62-968a-e6622f242a7e");
        final SendMessageCommand sendMessageCommand = new SendMessageCommand(
                UUID.fromString("3cf2b4ee-7843-4c26-99ac-93b64376f36e"),
                new SendMessagePayload(UUID.fromString("63fb006a-24e9-4a17-8add-b0ab3472ff94"),
                        bidId, "message"));
        final Message expectedResult = Message.builder()
                .thread(Thread.builder()
                        .sender(ConnectProfile.builder()
                                .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                .build())
                        .recipient(ConnectProfile.builder()
                                .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                .build())
                        .bid(Bid.builder()
                                .owner(ConnectProfile.builder()
                                        .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                        .build())
                                .build())
                        .lastMessageAt(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC))
                        .messages(Set.of())
                        .status(ThreadStatus.OPEN)
                        .build())
                .text("message")
                .media(Media.builder().build())
                .read(false)
                .keywords(Set.of("value"))
                .build();

        // Configure BidRepository.findById(...).
        final Optional<Bid> bidOptional = Optional.of(Bid.builder()
                .owner(ConnectProfile.builder()
                        .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                        .build())
                .build());
        when(mockBidRepository.findById(bidId))
                .thenReturn(bidOptional);

        // Configure ThreadRepo.findByProfile(...).
        final Optional<Thread> thread = Optional.of(Thread.builder()
                .sender(ConnectProfile.builder()
                        .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                        .build())
                .recipient(ConnectProfile.builder()
                        .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                        .build())
                .bid(Bid.builder()
                        .owner(ConnectProfile.builder()
                                .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                .build())
                        .build())
                .lastMessageAt(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC))
                .lastMessage(Message.builder()
                        .text("message")
                        .media(Media.builder().build())
                        .read(false)
                        .keywords(Set.of("value"))
                        .build())
                .messages(Set.of(Message.builder()
                        .text("message")
                        .media(Media.builder().build())
                        .read(false)
                        .keywords(Set.of("value"))
                        .build()))
                .status(ThreadStatus.OPEN)
                .build());
        when(mockThreadRepo.findByProfile(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"),
                UUID.fromString("3cf2b4ee-7843-4c26-99ac-93b64376f36e"),
                bidId)).thenReturn(thread);

        // Configure ThreadRepo.save(...).
        final Thread thread1 = Thread.builder()
                .sender(ConnectProfile.builder()
                        .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                        .build())
                .recipient(ConnectProfile.builder()
                        .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                        .build())
                .bid(Bid.builder()
                        .owner(ConnectProfile.builder()
                                .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                .build())
                        .build())
                .lastMessageAt(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC))
                .lastMessage(Message.builder()
                        .text("message")
                        .media(Media.builder().build())
                        .read(false)
                        .keywords(Set.of("value"))
                        .build())
                .messages(Set.of(Message.builder()
                        .text("message")
                        .media(Media.builder().build())
                        .read(false)
                        .keywords(Set.of("value"))
                        .build()))
                .status(ThreadStatus.OPEN)
                .build();
        when(mockThreadRepo.save(Thread.builder()
                .sender(ConnectProfile.builder()
                        .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                        .build())
                .recipient(ConnectProfile.builder()
                        .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                        .build())
                .bid(Bid.builder()
                        .owner(ConnectProfile.builder()
                                .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                .build())
                        .build())
                .lastMessageAt(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC))
                .lastMessage(Message.builder()
                        .text("message")
                        .media(Media.builder().build())
                        .read(false)
                        .keywords(Set.of("value"))
                        .build())
                .messages(Set.of(Message.builder()
                        .text("message")
                        .media(Media.builder().build())
                        .read(false)
                        .keywords(Set.of("value"))
                        .build()))
                .status(ThreadStatus.OPEN)
                .build())).thenReturn(thread1);

        when(mockKeywordService.extractFrom("message")).thenReturn(Set.of("value"));

        // Configure MessageRepo.save(...).
        final Message message = Message.builder()
                .thread(Thread.builder()
                        .sender(ConnectProfile.builder()
                                .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                .build())
                        .recipient(ConnectProfile.builder()
                                .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                .build())
                        .bid(Bid.builder()
                                .owner(ConnectProfile.builder()
                                        .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                        .build())
                                .build())
                        .lastMessageAt(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC))
                        .messages(Set.of())
                        .status(ThreadStatus.OPEN)
                        .build())
                .text("message")
                .media(Media.builder().build())
                .read(false)
                .keywords(Set.of("value"))
                .build();
        when(mockMessageRepo.save(Message.builder()
                .thread(Thread.builder()
                        .sender(ConnectProfile.builder()
                                .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                .build())
                        .recipient(ConnectProfile.builder()
                                .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                .build())
                        .bid(Bid.builder()
                                .owner(ConnectProfile.builder()
                                        .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                        .build())
                                .build())
                        .lastMessageAt(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC))
                        .messages(Set.of())
                        .status(ThreadStatus.OPEN)
                        .build())
                .text("message")
                .media(Media.builder().build())
                .read(false)
                .keywords(Set.of("value"))
                .build())).thenReturn(message);

        // Run the test
        final Message result = messageServiceUnderTest.send(sendMessageCommand);

        // Verify the results
        assertThat(result).usingRecursiveComparison().isEqualTo(expectedResult);
    }

    @Test
    void testSend_BidRepositoryReturnsAbsent() {
        // Setup
        UUID bidId = UUID.fromString("eb9a0e47-0cf0-4c62-968a-e6622f242a7e");
        final SendMessageCommand sendMessageCommand = new SendMessageCommand(
                UUID.fromString("3cf2b4ee-7843-4c26-99ac-93b64376f36e"),
                new SendMessagePayload(UUID.fromString("63fb006a-24e9-4a17-8add-b0ab3472ff94"),
                        bidId, "message"));
        final Message expectedResult = Message.builder()
                .thread(Thread.builder()
                        .sender(ConnectProfile.builder()
                                .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                .build())
                        .recipient(ConnectProfile.builder()
                                .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                .build())
                        .bid(Bid.builder()
                                .owner(ConnectProfile.builder()
                                        .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                        .build())
                                .build())
                        .lastMessageAt(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC))
                        .messages(Set.of())
                        .status(ThreadStatus.OPEN)
                        .build())
                .text("message")
                .media(Media.builder().build())
                .read(false)
                .keywords(Set.of("value"))
                .build();
        when(mockBidRepository.findById(bidId))
                .thenReturn(Optional.empty());

        // Configure ProfileRepo.findParentFrom(...).
        final Optional<ConnectProfile> connectProfile = Optional.of(ConnectProfile.builder()
                .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                .build());
        when(mockProfileRepo.findParentFrom(UUID.fromString("63fb006a-24e9-4a17-8add-b0ab3472ff94")))
                .thenReturn(connectProfile);

        // Configure ThreadRepo.findByProfile(...).
        final Optional<Thread> thread = Optional.of(Thread.builder()
                .sender(ConnectProfile.builder()
                        .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                        .build())
                .recipient(ConnectProfile.builder()
                        .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                        .build())
                .bid(Bid.builder()
                        .owner(ConnectProfile.builder()
                                .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                .build())
                        .build())
                .lastMessageAt(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC))
                .lastMessage(Message.builder()
                        .text("message")
                        .media(Media.builder().build())
                        .read(false)
                        .keywords(Set.of("value"))
                        .build())
                .messages(Set.of(Message.builder()
                        .text("message")
                        .media(Media.builder().build())
                        .read(false)
                        .keywords(Set.of("value"))
                        .build()))
                .status(ThreadStatus.OPEN)
                .build());
        when(mockThreadRepo.findByProfile(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"),
                UUID.fromString("3cf2b4ee-7843-4c26-99ac-93b64376f36e"),
                bidId)).thenReturn(thread);

        // Configure ThreadRepo.save(...).
        final Thread thread1 = Thread.builder()
                .sender(ConnectProfile.builder()
                        .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                        .build())
                .recipient(ConnectProfile.builder()
                        .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                        .build())
                .bid(Bid.builder()
                        .owner(ConnectProfile.builder()
                                .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                .build())
                        .build())
                .lastMessageAt(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC))
                .lastMessage(Message.builder()
                        .text("message")
                        .media(Media.builder().build())
                        .read(false)
                        .keywords(Set.of("value"))
                        .build())
                .messages(Set.of(Message.builder()
                        .text("message")
                        .media(Media.builder().build())
                        .read(false)
                        .keywords(Set.of("value"))
                        .build()))
                .status(ThreadStatus.OPEN)
                .build();
        when(mockThreadRepo.save(Thread.builder()
                .sender(ConnectProfile.builder()
                        .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                        .build())
                .recipient(ConnectProfile.builder()
                        .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                        .build())
                .bid(Bid.builder()
                        .owner(ConnectProfile.builder()
                                .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                .build())
                        .build())
                .lastMessageAt(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC))
                .lastMessage(Message.builder()
                        .text("message")
                        .media(Media.builder().build())
                        .read(false)
                        .keywords(Set.of("value"))
                        .build())
                .messages(Set.of(Message.builder()
                        .text("message")
                        .media(Media.builder().build())
                        .read(false)
                        .keywords(Set.of("value"))
                        .build()))
                .status(ThreadStatus.OPEN)
                .build())).thenReturn(thread1);

        when(mockKeywordService.extractFrom("message")).thenReturn(Set.of("value"));

        // Configure MessageRepo.save(...).
        final Message message = Message.builder()
                .thread(Thread.builder()
                        .sender(ConnectProfile.builder()
                                .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                .build())
                        .recipient(ConnectProfile.builder()
                                .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                .build())
                        .bid(Bid.builder()
                                .owner(ConnectProfile.builder()
                                        .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                        .build())
                                .build())
                        .lastMessageAt(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC))
                        .messages(Set.of())
                        .status(ThreadStatus.OPEN)
                        .build())
                .text("message")
                .media(Media.builder().build())
                .read(false)
                .keywords(Set.of("value"))
                .build();
        when(mockMessageRepo.save(Message.builder()
                .thread(Thread.builder()
                        .sender(ConnectProfile.builder()
                                .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                .build())
                        .recipient(ConnectProfile.builder()
                                .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                .build())
                        .bid(Bid.builder()
                                .owner(ConnectProfile.builder()
                                        .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                        .build())
                                .build())
                        .lastMessageAt(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC))
                        .messages(Set.of())
                        .status(ThreadStatus.OPEN)
                        .build())
                .text("message")
                .media(Media.builder().build())
                .read(false)
                .keywords(Set.of("value"))
                .build())).thenReturn(message);

        // Run the test
        final Message result = messageServiceUnderTest.send(sendMessageCommand);

        // Verify the results
        assertThat(result)
                .usingRecursiveComparison()
                .isEqualTo(expectedResult);
    }

    @Test
    void testSend_ProfileRepoReturnsAbsent() {
        // Setup
        UUID bidId = UUID.fromString("eb9a0e47-0cf0-4c62-968a-e6622f242a7e");
        UUID recipientId = UUID.fromString("63fb006a-24e9-4a17-8add-b0ab3472ff94");
        final SendMessageCommand sendMessageCommand = new SendMessageCommand(
                UUID.fromString("3cf2b4ee-7843-4c26-99ac-93b64376f36e"),
                new SendMessagePayload(recipientId,
                        bidId, "message"));
        when(mockBidRepository.findById(bidId))
                .thenReturn(Optional.empty());
        when(mockProfileRepo.findParentFrom(recipientId))
                .thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(() -> messageServiceUnderTest.send(sendMessageCommand))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void testSend_ThreadRepoFindByProfileReturnsAbsent() {
        // Setup
        UUID bidId = UUID.fromString("eb9a0e47-0cf0-4c62-968a-e6622f242a7e");
        UUID recipientId = UUID.fromString("63fb006a-24e9-4a17-8add-b0ab3472ff94");
        UUID senderId = UUID.fromString("3cf2b4ee-7843-4c26-99ac-93b64376f36e");
        final SendMessageCommand sendMessageCommand = new SendMessageCommand(
                senderId,
                new SendMessagePayload(recipientId,
                        bidId, "message"));
        final Message expectedResult = Message.builder()
                .thread(Thread.builder()
                        .sender(ConnectProfile.builder()
                                .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                .build())
                        .recipient(ConnectProfile.builder()
                                .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                .build())
                        .bid(Bid.builder()
                                .owner(ConnectProfile.builder()
                                        .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                        .build())
                                .build())
                        .lastMessageAt(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC))
                        .messages(Set.of())
                        .status(ThreadStatus.OPEN)
                        .build())
                .text("message")
                .media(Media.builder().build())
                .read(false)
                .keywords(Set.of("value"))
                .build();

        // Configure BidRepository.findById(...).
        final Optional<Bid> bidOptional = Optional.of(Bid.builder()
                .owner(ConnectProfile.builder()
                        .id(recipientId)
                        .build())
                .build());
        when(mockBidRepository.findById(bidId))
                .thenReturn(bidOptional);

        when(mockThreadRepo.findByProfile(recipientId,
                senderId,
                bidId)).thenReturn(Optional.empty());

        // Configure ProfileRepo.findParentFrom(...).
        final Optional<ConnectProfile> connectProfile = Optional.of(ConnectProfile.builder()
                .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                .build());

        when(mockProfileRepo.findParentFrom(senderId))
                .thenReturn(connectProfile);

        // Configure ThreadRepo.save(...).
        final Thread thread = Thread.builder()
                .sender(ConnectProfile.builder()
                        .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                        .build())
                .recipient(ConnectProfile.builder()
                        .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                        .build())
                .bid(Bid.builder()
                        .owner(ConnectProfile.builder()
                                .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                .build())
                        .build())
                .lastMessageAt(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC))
                .lastMessage(Message.builder()
                        .text("message")
                        .media(Media.builder().build())
                        .read(false)
                        .keywords(Set.of("value"))
                        .build())
                .messages(Set.of(Message.builder()
                        .text("message")
                        .media(Media.builder().build())
                        .read(false)
                        .keywords(Set.of("value"))
                        .build()))
                .status(ThreadStatus.OPEN)
                .build();
        when(mockThreadRepo.save(Thread.builder()
                .sender(ConnectProfile.builder()
                        .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                        .build())
                .recipient(ConnectProfile.builder()
                        .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                        .build())
                .bid(Bid.builder()
                        .owner(ConnectProfile.builder()
                                .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                .build())
                        .build())
                .lastMessageAt(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC))
                .lastMessage(Message.builder()
                        .text("message")
                        .media(Media.builder().build())
                        .read(false)
                        .keywords(Set.of("value"))
                        .build())
                .messages(Set.of(Message.builder()
                        .text("message")
                        .media(Media.builder().build())
                        .read(false)
                        .keywords(Set.of("value"))
                        .build()))
                .status(ThreadStatus.OPEN)
                .build())).thenReturn(thread);

        when(mockKeywordService.extractFrom("message")).thenReturn(Set.of("value"));

        // Configure MessageRepo.save(...).
        final Message message = Message.builder()
                .thread(Thread.builder()
                        .sender(ConnectProfile.builder()
                                .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                .build())
                        .recipient(ConnectProfile.builder()
                                .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                .build())
                        .bid(Bid.builder()
                                .owner(ConnectProfile.builder()
                                        .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                        .build())
                                .build())
                        .lastMessageAt(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC))
                        .messages(Set.of())
                        .status(ThreadStatus.OPEN)
                        .build())
                .text("message")
                .media(Media.builder().build())
                .read(false)
                .keywords(Set.of("value"))
                .build();
        when(mockMessageRepo.save(Message.builder()
                .thread(Thread.builder()
                        .sender(ConnectProfile.builder()
                                .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                .build())
                        .recipient(ConnectProfile.builder()
                                .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                .build())
                        .bid(Bid.builder()
                                .owner(ConnectProfile.builder()
                                        .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                        .build())
                                .build())
                        .lastMessageAt(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC))
                        .messages(Set.of())
                        .status(ThreadStatus.OPEN)
                        .build())
                .text("message")
                .media(Media.builder().build())
                .read(false)
                .keywords(Set.of("value"))
                .build())).thenReturn(message);

        // Run the test
        final Message result = messageServiceUnderTest.send(sendMessageCommand);

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }


    @Test
    void testFindOrInitThread() {
        // Setup
        UUID bidId = UUID.fromString("eb9a0e47-0cf0-4c62-968a-e6622f242a7e");
        final SendMessageCommand sendMessageCommand = new SendMessageCommand(
                UUID.fromString("3cf2b4ee-7843-4c26-99ac-93b64376f36e"),
                new SendMessagePayload(UUID.fromString("63fb006a-24e9-4a17-8add-b0ab3472ff94"),
                        bidId, "message"));
        final Thread expectedResult = Thread.builder()
                .sender(ConnectProfile.builder()
                        .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                        .build())
                .recipient(ConnectProfile.builder()
                        .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                        .build())
                .bid(Bid.builder()
                        .owner(ConnectProfile.builder()
                                .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                .build())
                        .build())
                .lastMessageAt(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC))
                .lastMessage(Message.builder()
                        .text("message")
                        .media(Media.builder().build())
                        .read(false)
                        .keywords(Set.of("value"))
                        .build())
                .messages(Set.of(Message.builder()
                        .text("message")
                        .media(Media.builder().build())
                        .read(false)
                        .keywords(Set.of("value"))
                        .build()))
                .status(ThreadStatus.OPEN)
                .build();

        // Configure BidRepository.findById(...).
        final Optional<Bid> bidOptional = Optional.of(Bid.builder()
                .owner(ConnectProfile.builder()
                        .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                        .build())
                .build());
        when(mockBidRepository.findById(bidId))
                .thenReturn(bidOptional);

        // Configure ThreadRepo.findByProfile(...).
        final Optional<Thread> thread = Optional.of(Thread.builder()
                .sender(ConnectProfile.builder()
                        .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                        .build())
                .recipient(ConnectProfile.builder()
                        .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                        .build())
                .bid(Bid.builder()
                        .owner(ConnectProfile.builder()
                                .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                .build())
                        .build())
                .lastMessageAt(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC))
                .lastMessage(Message.builder()
                        .text("message")
                        .media(Media.builder().build())
                        .read(false)
                        .keywords(Set.of("value"))
                        .build())
                .messages(Set.of(Message.builder()
                        .text("message")
                        .media(Media.builder().build())
                        .read(false)
                        .keywords(Set.of("value"))
                        .build()))
                .status(ThreadStatus.OPEN)
                .build());
        when(mockThreadRepo.findByProfile(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"),
                UUID.fromString("3cf2b4ee-7843-4c26-99ac-93b64376f36e"),
                bidId)).thenReturn(thread);

        // Run the test
        final Thread result = messageServiceUnderTest.findOrInitThread(sendMessageCommand);

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testFindOrInitThread_BidRepositoryReturnsAbsent() {
        // Setup
        UUID bidId = UUID.fromString("eb9a0e47-0cf0-4c62-968a-e6622f242a7e");
        final SendMessageCommand sendMessageCommand = new SendMessageCommand(
                UUID.fromString("3cf2b4ee-7843-4c26-99ac-93b64376f36e"),
                new SendMessagePayload(UUID.fromString("63fb006a-24e9-4a17-8add-b0ab3472ff94"),
                        bidId, "message"));
        final Thread expectedResult = Thread.builder()
                .sender(ConnectProfile.builder()
                        .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                        .build())
                .recipient(ConnectProfile.builder()
                        .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                        .build())
                .bid(Bid.builder()
                        .owner(ConnectProfile.builder()
                                .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                .build())
                        .build())
                .lastMessageAt(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC))
                .lastMessage(Message.builder()
                        .text("message")
                        .media(Media.builder().build())
                        .read(false)
                        .keywords(Set.of("value"))
                        .build())
                .messages(Set.of(Message.builder()
                        .text("message")
                        .media(Media.builder().build())
                        .read(false)
                        .keywords(Set.of("value"))
                        .build()))
                .status(ThreadStatus.OPEN)
                .build();
        when(mockBidRepository.findById(bidId))
                .thenReturn(Optional.empty());

        // Configure ProfileRepo.findParentFrom(...).
        final Optional<ConnectProfile> connectProfile = Optional.of(ConnectProfile.builder()
                .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                .build());
        when(mockProfileRepo.findParentFrom(UUID.fromString("63fb006a-24e9-4a17-8add-b0ab3472ff94")))
                .thenReturn(connectProfile);

        // Configure ThreadRepo.findByProfile(...).
        final Optional<Thread> thread = Optional.of(Thread.builder()
                .sender(ConnectProfile.builder()
                        .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                        .build())
                .recipient(ConnectProfile.builder()
                        .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                        .build())
                .bid(Bid.builder()
                        .owner(ConnectProfile.builder()
                                .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                .build())
                        .build())
                .lastMessageAt(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC))
                .lastMessage(Message.builder()
                        .text("message")
                        .media(Media.builder().build())
                        .read(false)
                        .keywords(Set.of("value"))
                        .build())
                .messages(Set.of(Message.builder()
                        .text("message")
                        .media(Media.builder().build())
                        .read(false)
                        .keywords(Set.of("value"))
                        .build()))
                .status(ThreadStatus.OPEN)
                .build());
        when(mockThreadRepo.findByProfile(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"),
                UUID.fromString("3cf2b4ee-7843-4c26-99ac-93b64376f36e"),
                bidId)).thenReturn(thread);

        // Run the test
        final Thread result = messageServiceUnderTest.findOrInitThread(sendMessageCommand);

        // Verify the results
        assertThat(result)
                .usingRecursiveComparison()
                .ignoringFields("lastMessageAt")
                .isEqualTo(expectedResult);
    }

    @Test
    void testFindOrInitThread_ProfileRepoReturnsAbsent() {
        // Setup
        UUID bidId = UUID.fromString("eb9a0e47-0cf0-4c62-968a-e6622f242a7e");
        final SendMessageCommand sendMessageCommand = new SendMessageCommand(
                UUID.fromString("3cf2b4ee-7843-4c26-99ac-93b64376f36e"),
                new SendMessagePayload(UUID.fromString("63fb006a-24e9-4a17-8add-b0ab3472ff94"),
                        bidId, "message"));
        when(mockBidRepository.findById(bidId))
                .thenReturn(Optional.empty());
        when(mockProfileRepo.findParentFrom(UUID.fromString("63fb006a-24e9-4a17-8add-b0ab3472ff94")))
                .thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(() -> messageServiceUnderTest.findOrInitThread(sendMessageCommand))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void testFindOrInitThread_ThreadRepoReturnsAbsent() {
        // Setup
        UUID bidId = UUID.fromString("eb9a0e47-0cf0-4c62-968a-e6622f242a7e");
        UUID senderId = UUID.fromString("3cf2b4ee-7843-4c26-99ac-93b64376f36e");
        UUID recipientId = UUID.fromString("63fb006a-24e9-4a17-8add-b0ab3472ff94");
        final SendMessageCommand sendMessageCommand = new SendMessageCommand(
                senderId,
                new SendMessagePayload(recipientId,
                        bidId, "message"));
        final Thread expectedResult = Thread.builder()
                .sender(ConnectProfile.builder()
                        .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                        .build())
                .recipient(ConnectProfile.builder()
                        .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                        .build())
                .bid(Bid.builder()
                        .owner(ConnectProfile.builder()
                                .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                .build())
                        .build())
                .lastMessageAt(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC))
                .lastMessage(Message.builder()
                        .text("message")
                        .media(Media.builder().build())
                        .read(false)
                        .keywords(Set.of("value"))
                        .build())
                .messages(Set.of(Message.builder()
                        .text("message")
                        .media(Media.builder().build())
                        .read(false)
                        .keywords(Set.of("value"))
                        .build()))
                .status(ThreadStatus.OPEN)
                .build();

        // Configure BidRepository.findById(...).
        final Optional<Bid> bidOptional = Optional.of(Bid.builder()
                .owner(ConnectProfile.builder()
                        .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                        .build())
                .build());
        when(mockBidRepository.findById(bidId))
                .thenReturn(bidOptional);

        when(mockThreadRepo.findByProfile(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"),
                senderId,
                bidId)).thenReturn(Optional.empty());

        // Configure ProfileRepo.findParentFrom(...).
        final Optional<ConnectProfile> connectProfile = Optional.of(ConnectProfile.builder()
                .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                .build());
        when(mockProfileRepo.findParentFrom(senderId))
                .thenReturn(connectProfile);

        // Run the test
        final Thread result = messageServiceUnderTest.findOrInitThread(sendMessageCommand);

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testGetThreadsFor() {
        // Setup
        when(mockThreadRepo.findThreadsFor(UUID.fromString("3558ecf2-f882-4933-8417-670f2b98373e"),
                "3558ecf2-f882-4933-8417-670f2b98373e")).thenReturn(List.of());

        // Run the test
        final List<ProfileThreadItem> result = messageServiceUnderTest.getThreadsFor(
                UUID.fromString("3558ecf2-f882-4933-8417-670f2b98373e"));

        // Verify the results
    }

    @Test
    void testGetThreadsFor_ThreadRepoReturnsNoItems() {
        // Setup
        when(mockThreadRepo.findThreadsFor(UUID.fromString("3558ecf2-f882-4933-8417-670f2b98373e"),
                "3558ecf2-f882-4933-8417-670f2b98373e")).thenReturn(Collections.emptyList());

        // Run the test
        final List<ProfileThreadItem> result = messageServiceUnderTest.getThreadsFor(
                UUID.fromString("3558ecf2-f882-4933-8417-670f2b98373e"));

        // Verify the results
        assertThat(result).isEqualTo(Collections.emptyList());
    }

    @Test
    void testGetMessagesFrom() {
        // Setup
        final List<Message> expectedResult = List.of(Message.builder()
                .thread(Thread.builder()
                        .sender(ConnectProfile.builder()
                                .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                .build())
                        .recipient(ConnectProfile.builder()
                                .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                .build())
                        .bid(Bid.builder()
                                .owner(ConnectProfile.builder()
                                        .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                        .build())
                                .build())
                        .lastMessageAt(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC))
                        .messages(Set.of())
                        .status(ThreadStatus.OPEN)
                        .build())
                .text("message")
                .media(Media.builder().build())
                .read(false)
                .keywords(Set.of("value"))
                .build());

        // Configure MessageRepo.findFullByThread(...).
        final List<Message> messages = List.of(Message.builder()
                .thread(Thread.builder()
                        .sender(ConnectProfile.builder()
                                .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                .build())
                        .recipient(ConnectProfile.builder()
                                .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                .build())
                        .bid(Bid.builder()
                                .owner(ConnectProfile.builder()
                                        .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                        .build())
                                .build())
                        .lastMessageAt(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC))
                        .messages(Set.of())
                        .status(ThreadStatus.OPEN)
                        .build())
                .text("message")
                .media(Media.builder().build())
                .read(false)
                .keywords(Set.of("value"))
                .build());
        when(mockMessageRepo.findFullByThread(UUID.fromString("db637074-458a-4c6a-bb82-808ef9035212")))
                .thenReturn(messages);

        // Run the test
        final List<Message> result = messageServiceUnderTest.getMessagesFrom(
                UUID.fromString("db637074-458a-4c6a-bb82-808ef9035212"));

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testGetMessagesFrom_MessageRepoReturnsNoItems() {
        // Setup
        when(mockMessageRepo.findFullByThread(UUID.fromString("db637074-458a-4c6a-bb82-808ef9035212")))
                .thenReturn(Collections.emptyList());

        // Run the test
        final List<Message> result = messageServiceUnderTest.getMessagesFrom(
                UUID.fromString("db637074-458a-4c6a-bb82-808ef9035212"));

        // Verify the results
        assertThat(result).isEqualTo(Collections.emptyList());
    }

    @Test
    void testCreate() {
        // Setup
        UUID threadId = UUID.fromString("692cc192-6266-4e9d-9243-d03c9fe75804");
        final ThreadMessageCommand threadMessageCommand = new ThreadMessageCommand(
                threadId, new TextPayload("message"));
        final Thread expectedResult = Thread.builder()
                .sender(ConnectProfile.builder()
                        .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                        .build())
                .recipient(ConnectProfile.builder()
                        .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                        .build())
                .bid(Bid.builder()
                        .owner(ConnectProfile.builder()
                                .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                .build())
                        .build())
                .lastMessageAt(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC))
                .lastMessage(Message.builder()
                        .text("message")
                        .media(Media.builder().build())
                        .read(false)
                        .keywords(Set.of("value"))
                        .build())
                .messages(Set.of(Message.builder()
                        .text("message")
                        .media(Media.builder().build())
                        .read(false)
                        .keywords(Set.of("value"))
                        .build()))
                .status(ThreadStatus.OPEN)
                .build();

        // Configure ThreadRepo.findById(...).
        final Optional<Thread> thread = Optional.of(Thread.builder()
                .sender(ConnectProfile.builder()
                        .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                        .build())
                .recipient(ConnectProfile.builder()
                        .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                        .build())
                .bid(Bid.builder()
                        .owner(ConnectProfile.builder()
                                .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                .build())
                        .build())
                .lastMessageAt(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC))
                .lastMessage(Message.builder()
                        .text("message")
                        .media(Media.builder().build())
                        .read(false)
                        .keywords(Set.of("value"))
                        .build())
                .messages(Set.of(Message.builder()
                        .text("message")
                        .media(Media.builder().build())
                        .read(false)
                        .keywords(Set.of("value"))
                        .build()))
                .status(ThreadStatus.OPEN)
                .build());
        when(mockThreadRepo.findById(threadId)).thenReturn(thread);

        when(mockKeywordService.extractFrom("message")).thenReturn(Set.of("value"));

        // Configure MessageRepo.save(...).
        final Message message = Message.builder()
                .thread(Thread.builder()
                        .sender(ConnectProfile.builder()
                                .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                .build())
                        .recipient(ConnectProfile.builder()
                                .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                .build())
                        .bid(Bid.builder()
                                .owner(ConnectProfile.builder()
                                        .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                        .build())
                                .build())
                        .lastMessageAt(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC))
                        .messages(Set.of())
                        .status(ThreadStatus.OPEN)
                        .build())
                .text("message")
                .media(Media.builder().build())
                .read(false)
                .keywords(Set.of("value"))
                .build();
        when(mockMessageRepo.save(Message.builder()
                .thread(Thread.builder()
                        .sender(ConnectProfile.builder()
                                .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                .build())
                        .recipient(ConnectProfile.builder()
                                .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                .build())
                        .bid(Bid.builder()
                                .owner(ConnectProfile.builder()
                                        .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                        .build())
                                .build())
                        .lastMessageAt(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC))
                        .messages(Set.of())
                        .status(ThreadStatus.OPEN)
                        .build())
                .text("message")
                .media(Media.builder().build())
                .read(false)
                .keywords(Set.of("value"))
                .build())).thenReturn(message);

        // Configure ThreadRepo.save(...).
        final Thread thread1 = Thread.builder()
                .sender(ConnectProfile.builder()
                        .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                        .build())
                .recipient(ConnectProfile.builder()
                        .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                        .build())
                .bid(Bid.builder()
                        .owner(ConnectProfile.builder()
                                .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                .build())
                        .build())
                .lastMessageAt(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC))
                .lastMessage(Message.builder()
                        .text("message")
                        .media(Media.builder().build())
                        .read(false)
                        .keywords(Set.of("value"))
                        .build())
                .messages(Set.of(Message.builder()
                        .text("message")
                        .media(Media.builder().build())
                        .read(false)
                        .keywords(Set.of("value"))
                        .build()))
                .status(ThreadStatus.OPEN)
                .build();
        when(mockThreadRepo.save(Thread.builder()
                .sender(ConnectProfile.builder()
                        .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                        .build())
                .recipient(ConnectProfile.builder()
                        .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                        .build())
                .bid(Bid.builder()
                        .owner(ConnectProfile.builder()
                                .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                .build())
                        .build())
                .lastMessageAt(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC))
                .lastMessage(Message.builder()
                        .text("message")
                        .media(Media.builder().build())
                        .read(false)
                        .keywords(Set.of("value"))
                        .build())
                .messages(Set.of(Message.builder()
                        .text("message")
                        .media(Media.builder().build())
                        .read(false)
                        .keywords(Set.of("value"))
                        .build()))
                .status(ThreadStatus.OPEN)
                .build())).thenReturn(thread1);

        // Run the test
        final Thread result = messageServiceUnderTest.create(threadMessageCommand);

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testCreate_ThreadRepoFindByIdReturnsAbsent() {
        // Setup
        UUID threadId = UUID.randomUUID();
        final ThreadMessageCommand threadMessageCommand = new ThreadMessageCommand(
                threadId, new TextPayload("message"));
        when(mockThreadRepo.findById(threadId))
                .thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(() -> messageServiceUnderTest.create(threadMessageCommand))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void testCreate_KeywordServiceReturnsNoItems() {
        // Setup
        UUID threadId = UUID.fromString("692cc192-6266-4e9d-9243-d03c9fe75804");
        final ThreadMessageCommand threadMessageCommand = new ThreadMessageCommand(
                threadId, new TextPayload("message"));
        final Thread expectedResult = Thread.builder()
                .sender(ConnectProfile.builder()
                        .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                        .build())
                .recipient(ConnectProfile.builder()
                        .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                        .build())
                .bid(Bid.builder()
                        .owner(ConnectProfile.builder()
                                .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                .build())
                        .build())
                .lastMessageAt(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC))
                .lastMessage(Message.builder()
                        .text("message")
                        .media(Media.builder().build())
                        .read(false)
                        .keywords(Set.of("value"))
                        .build())
                .messages(Set.of(Message.builder()
                        .text("message")
                        .media(Media.builder().build())
                        .read(false)
                        .keywords(Set.of("value"))
                        .build()))
                .status(ThreadStatus.OPEN)
                .build();

        // Configure ThreadRepo.findById(...).
        final Optional<Thread> thread = Optional.of(Thread.builder()
                .sender(ConnectProfile.builder()
                        .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                        .build())
                .recipient(ConnectProfile.builder()
                        .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                        .build())
                .bid(Bid.builder()
                        .owner(ConnectProfile.builder()
                                .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                .build())
                        .build())
                .lastMessageAt(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC))
                .lastMessage(Message.builder()
                        .text("message")
                        .media(Media.builder().build())
                        .read(false)
                        .keywords(Set.of("value"))
                        .build())
                .messages(Set.of(Message.builder()
                        .text("message")
                        .media(Media.builder().build())
                        .read(false)
                        .keywords(Set.of("value"))
                        .build()))
                .status(ThreadStatus.OPEN)
                .build());
        when(mockThreadRepo.findById(threadId)).thenReturn(thread);

        when(mockKeywordService.extractFrom("message")).thenReturn(Collections.emptySet());

        // Configure MessageRepo.save(...).
        final Message message = Message.builder()
                .thread(Thread.builder()
                        .sender(ConnectProfile.builder()
                                .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                .build())
                        .recipient(ConnectProfile.builder()
                                .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                .build())
                        .bid(Bid.builder()
                                .owner(ConnectProfile.builder()
                                        .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                        .build())
                                .build())
                        .lastMessageAt(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC))
                        .messages(Set.of())
                        .status(ThreadStatus.OPEN)
                        .build())
                .text("message")
                .media(Media.builder().build())
                .read(false)
                .keywords(Set.of("value"))
                .build();
        when(mockMessageRepo.save(Message.builder()
                .thread(Thread.builder()
                        .sender(ConnectProfile.builder()
                                .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                .build())
                        .recipient(ConnectProfile.builder()
                                .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                .build())
                        .bid(Bid.builder()
                                .owner(ConnectProfile.builder()
                                        .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                        .build())
                                .build())
                        .lastMessageAt(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC))
                        .messages(Set.of())
                        .status(ThreadStatus.OPEN)
                        .build())
                .text("message")
                .media(Media.builder().build())
                .read(false)
                .keywords(Set.of("value"))
                .build())).thenReturn(message);

        // Configure ThreadRepo.save(...).
        final Thread thread1 = Thread.builder()
                .sender(ConnectProfile.builder()
                        .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                        .build())
                .recipient(ConnectProfile.builder()
                        .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                        .build())
                .bid(Bid.builder()
                        .owner(ConnectProfile.builder()
                                .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                .build())
                        .build())
                .lastMessageAt(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC))
                .lastMessage(Message.builder()
                        .text("message")
                        .media(Media.builder().build())
                        .read(false)
                        .keywords(Set.of("value"))
                        .build())
                .messages(Set.of(Message.builder()
                        .text("message")
                        .media(Media.builder().build())
                        .read(false)
                        .keywords(Set.of("value"))
                        .build()))
                .status(ThreadStatus.OPEN)
                .build();
        when(mockThreadRepo.save(Thread.builder()
                .sender(ConnectProfile.builder()
                        .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                        .build())
                .recipient(ConnectProfile.builder()
                        .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                        .build())
                .bid(Bid.builder()
                        .owner(ConnectProfile.builder()
                                .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                .build())
                        .build())
                .lastMessageAt(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC))
                .lastMessage(Message.builder()
                        .text("message")
                        .media(Media.builder().build())
                        .read(false)
                        .keywords(Set.of("value"))
                        .build())
                .messages(Set.of(Message.builder()
                        .text("message")
                        .media(Media.builder().build())
                        .read(false)
                        .keywords(Set.of("value"))
                        .build()))
                .status(ThreadStatus.OPEN)
                .build())).thenReturn(thread1);

        // Run the test
        final Thread result = messageServiceUnderTest.create(threadMessageCommand);

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testDelete() {
        // Setup
        final Message expectedResult = Message.builder()
                .thread(Thread.builder()
                        .sender(ConnectProfile.builder()
                                .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                .build())
                        .recipient(ConnectProfile.builder()
                                .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                .build())
                        .bid(Bid.builder()
                                .owner(ConnectProfile.builder()
                                        .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                        .build())
                                .build())
                        .lastMessageAt(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC))
                        .messages(Set.of())
                        .status(ThreadStatus.OPEN)
                        .build())
                .text("message")
                .media(Media.builder().build())
                .read(false)
                .keywords(Set.of("value"))
                .build();

        // Configure MessageRepo.findById(...).
        final Optional<Message> message = Optional.of(Message.builder()
                .thread(Thread.builder()
                        .sender(ConnectProfile.builder()
                                .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                .build())
                        .recipient(ConnectProfile.builder()
                                .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                .build())
                        .bid(Bid.builder()
                                .owner(ConnectProfile.builder()
                                        .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                        .build())
                                .build())
                        .lastMessageAt(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC))
                        .messages(Set.of())
                        .status(ThreadStatus.OPEN)
                        .build())
                .text("message")
                .media(Media.builder().build())
                .read(false)
                .keywords(Set.of("value"))
                .build());
        when(mockMessageRepo.findById(UUID.fromString("acf01f52-1fa5-4ce7-b45d-8331bbe3000a"))).thenReturn(message);

        // Run the test
        final Message result = messageServiceUnderTest.delete(UUID.fromString("acf01f52-1fa5-4ce7-b45d-8331bbe3000a"));

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
        verify(mockMediaService).delete(Media.builder().build());
        verify(mockMessageRepo).delete(Message.builder()
                .thread(Thread.builder()
                        .sender(ConnectProfile.builder()
                                .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                .build())
                        .recipient(ConnectProfile.builder()
                                .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                .build())
                        .bid(Bid.builder()
                                .owner(ConnectProfile.builder()
                                        .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                        .build())
                                .build())
                        .lastMessageAt(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC))
                        .messages(Set.of())
                        .status(ThreadStatus.OPEN)
                        .build())
                .text("message")
                .media(Media.builder().build())
                .read(false)
                .keywords(Set.of("value"))
                .build());
    }

    @Test
    void testDelete_MessageRepoFindByIdReturnsAbsent() {
        // Setup
        when(mockMessageRepo.findById(UUID.fromString("acf01f52-1fa5-4ce7-b45d-8331bbe3000a")))
                .thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(() -> messageServiceUnderTest.delete(
                UUID.fromString("acf01f52-1fa5-4ce7-b45d-8331bbe3000a"))).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void testDelete_MessageRepoDeleteThrowsOptimisticLockingFailureException() {
        // Setup
        // Configure MessageRepo.findById(...).
        final Optional<Message> message = Optional.of(Message.builder()
                .thread(Thread.builder()
                        .sender(ConnectProfile.builder()
                                .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                .build())
                        .recipient(ConnectProfile.builder()
                                .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                .build())
                        .bid(Bid.builder()
                                .owner(ConnectProfile.builder()
                                        .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                        .build())
                                .build())
                        .lastMessageAt(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC))
                        .messages(Set.of())
                        .status(ThreadStatus.OPEN)
                        .build())
                .text("message")
                .media(Media.builder().build())
                .read(false)
                .keywords(Set.of("value"))
                .build());
        when(mockMessageRepo.findById(UUID.fromString("acf01f52-1fa5-4ce7-b45d-8331bbe3000a"))).thenReturn(message);

        doThrow(OptimisticLockingFailureException.class).when(mockMessageRepo).delete(Message.builder()
                .thread(Thread.builder()
                        .sender(ConnectProfile.builder()
                                .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                .build())
                        .recipient(ConnectProfile.builder()
                                .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                .build())
                        .bid(Bid.builder()
                                .owner(ConnectProfile.builder()
                                        .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                        .build())
                                .build())
                        .lastMessageAt(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC))
                        .messages(Set.of())
                        .status(ThreadStatus.OPEN)
                        .build())
                .text("message")
                .media(Media.builder().build())
                .read(false)
                .keywords(Set.of("value"))
                .build());

        // Run the test
        assertThatThrownBy(() -> messageServiceUnderTest.delete(
                UUID.fromString("acf01f52-1fa5-4ce7-b45d-8331bbe3000a")))
                .isInstanceOf(OptimisticLockingFailureException.class);
        verify(mockMediaService).delete(Media.builder().build());
    }

    @Test
    void testDeleteThread() {
        // Setup
        final Thread expectedResult = Thread.builder()
                .sender(ConnectProfile.builder()
                        .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                        .build())
                .recipient(ConnectProfile.builder()
                        .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                        .build())
                .bid(Bid.builder()
                        .owner(ConnectProfile.builder()
                                .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                .build())
                        .build())
                .lastMessageAt(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC))
                .lastMessage(Message.builder()
                        .text("message")
                        .media(Media.builder().build())
                        .read(false)
                        .keywords(Set.of("value"))
                        .build())
                .messages(Set.of(Message.builder()
                        .text("message")
                        .media(Media.builder().build())
                        .read(false)
                        .keywords(Set.of("value"))
                        .build()))
                .status(ThreadStatus.OPEN)
                .build();

        // Configure ThreadRepo.findById(...).
        final Optional<Thread> thread = Optional.of(Thread.builder()
                .sender(ConnectProfile.builder()
                        .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                        .build())
                .recipient(ConnectProfile.builder()
                        .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                        .build())
                .bid(Bid.builder()
                        .owner(ConnectProfile.builder()
                                .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                .build())
                        .build())
                .lastMessageAt(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC))
                .lastMessage(Message.builder()
                        .text("message")
                        .media(Media.builder().build())
                        .read(false)
                        .keywords(Set.of("value"))
                        .build())
                .messages(Set.of(Message.builder()
                        .text("message")
                        .media(Media.builder().build())
                        .read(false)
                        .keywords(Set.of("value"))
                        .build()))
                .status(ThreadStatus.OPEN)
                .build());
        when(mockThreadRepo.findById(UUID.fromString("bb74e4c4-28dd-4de6-aff7-2c36d39fcfb9"))).thenReturn(thread);

        // Run the test
        final Thread result = messageServiceUnderTest.deleteThread(
                UUID.fromString("bb74e4c4-28dd-4de6-aff7-2c36d39fcfb9"));

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
        verify(mockMediaService).delete(Media.builder().build());
        verify(mockMessageRepo).delete(Message.builder()
                .thread(Thread.builder()
                        .sender(ConnectProfile.builder()
                                .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                .build())
                        .recipient(ConnectProfile.builder()
                                .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                .build())
                        .bid(Bid.builder()
                                .owner(ConnectProfile.builder()
                                        .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                        .build())
                                .build())
                        .lastMessageAt(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC))
                        .messages(Set.of())
                        .status(ThreadStatus.OPEN)
                        .build())
                .text("message")
                .media(Media.builder().build())
                .read(false)
                .keywords(Set.of("value"))
                .build());
        verify(mockThreadRepo).delete(Thread.builder()
                .sender(ConnectProfile.builder()
                        .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                        .build())
                .recipient(ConnectProfile.builder()
                        .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                        .build())
                .bid(Bid.builder()
                        .owner(ConnectProfile.builder()
                                .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                .build())
                        .build())
                .lastMessageAt(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC))
                .lastMessage(Message.builder()
                        .text("message")
                        .media(Media.builder().build())
                        .read(false)
                        .keywords(Set.of("value"))
                        .build())
                .messages(Set.of(Message.builder()
                        .text("message")
                        .media(Media.builder().build())
                        .read(false)
                        .keywords(Set.of("value"))
                        .build()))
                .status(ThreadStatus.OPEN)
                .build());
    }

    @Test
    void testDeleteThread_ThreadRepoFindByIdReturnsAbsent() {
        // Setup
        when(mockThreadRepo.findById(UUID.fromString("bb74e4c4-28dd-4de6-aff7-2c36d39fcfb9")))
                .thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(() -> messageServiceUnderTest.deleteThread(
                UUID.fromString("bb74e4c4-28dd-4de6-aff7-2c36d39fcfb9"))).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void testDeleteThread_MessageRepoThrowsOptimisticLockingFailureException() {
        // Setup
        // Configure ThreadRepo.findById(...).
        final Optional<Thread> thread = Optional.of(Thread.builder()
                .sender(ConnectProfile.builder()
                        .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                        .build())
                .recipient(ConnectProfile.builder()
                        .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                        .build())
                .bid(Bid.builder()
                        .owner(ConnectProfile.builder()
                                .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                .build())
                        .build())
                .lastMessageAt(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC))
                .lastMessage(Message.builder()
                        .text("message")
                        .media(Media.builder().build())
                        .read(false)
                        .keywords(Set.of("value"))
                        .build())
                .messages(Set.of(Message.builder()
                        .text("message")
                        .media(Media.builder().build())
                        .read(false)
                        .keywords(Set.of("value"))
                        .build()))
                .status(ThreadStatus.OPEN)
                .build());
        when(mockThreadRepo.findById(UUID.fromString("bb74e4c4-28dd-4de6-aff7-2c36d39fcfb9"))).thenReturn(thread);

        doThrow(OptimisticLockingFailureException.class).when(mockMessageRepo).delete(Message.builder()
                .thread(Thread.builder()
                        .sender(ConnectProfile.builder()
                                .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                .build())
                        .recipient(ConnectProfile.builder()
                                .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                .build())
                        .bid(Bid.builder()
                                .owner(ConnectProfile.builder()
                                        .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                        .build())
                                .build())
                        .lastMessageAt(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC))
                        .messages(Set.of())
                        .status(ThreadStatus.OPEN)
                        .build())
                .text("message")
                .media(Media.builder().build())
                .read(false)
                .keywords(Set.of("value"))
                .build());

        // Run the test
        assertThatThrownBy(() -> messageServiceUnderTest.deleteThread(
                UUID.fromString("bb74e4c4-28dd-4de6-aff7-2c36d39fcfb9")))
                .isInstanceOf(OptimisticLockingFailureException.class);
        verify(mockMediaService).delete(Media.builder().build());
    }

    @Test
    void testDeleteThread_ThreadRepoDeleteThrowsOptimisticLockingFailureException() {
        // Setup
        // Configure ThreadRepo.findById(...).
        final Optional<Thread> thread = Optional.of(Thread.builder()
                .sender(ConnectProfile.builder()
                        .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                        .build())
                .recipient(ConnectProfile.builder()
                        .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                        .build())
                .bid(Bid.builder()
                        .owner(ConnectProfile.builder()
                                .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                .build())
                        .build())
                .lastMessageAt(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC))
                .lastMessage(Message.builder()
                        .text("message")
                        .media(Media.builder().build())
                        .read(false)
                        .keywords(Set.of("value"))
                        .build())
                .messages(Set.of(Message.builder()
                        .text("message")
                        .media(Media.builder().build())
                        .read(false)
                        .keywords(Set.of("value"))
                        .build()))
                .status(ThreadStatus.OPEN)
                .build());
        when(mockThreadRepo.findById(UUID.fromString("bb74e4c4-28dd-4de6-aff7-2c36d39fcfb9"))).thenReturn(thread);

        doThrow(OptimisticLockingFailureException.class).when(mockThreadRepo).delete(Thread.builder()
                .sender(ConnectProfile.builder()
                        .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                        .build())
                .recipient(ConnectProfile.builder()
                        .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                        .build())
                .bid(Bid.builder()
                        .owner(ConnectProfile.builder()
                                .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                .build())
                        .build())
                .lastMessageAt(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC))
                .lastMessage(Message.builder()
                        .text("message")
                        .media(Media.builder().build())
                        .read(false)
                        .keywords(Set.of("value"))
                        .build())
                .messages(Set.of(Message.builder()
                        .text("message")
                        .media(Media.builder().build())
                        .read(false)
                        .keywords(Set.of("value"))
                        .build()))
                .status(ThreadStatus.OPEN)
                .build());

        // Run the test
        assertThatThrownBy(() -> messageServiceUnderTest.deleteThread(
                UUID.fromString("bb74e4c4-28dd-4de6-aff7-2c36d39fcfb9")))
                .isInstanceOf(OptimisticLockingFailureException.class);
        verify(mockMediaService).delete(Media.builder().build());
        verify(mockMessageRepo).delete(Message.builder()
                .thread(Thread.builder()
                        .sender(ConnectProfile.builder()
                                .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                .build())
                        .recipient(ConnectProfile.builder()
                                .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                .build())
                        .bid(Bid.builder()
                                .owner(ConnectProfile.builder()
                                        .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                        .build())
                                .build())
                        .lastMessageAt(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC))
                        .messages(Set.of())
                        .status(ThreadStatus.OPEN)
                        .build())
                .text("message")
                .media(Media.builder().build())
                .read(false)
                .keywords(Set.of("value"))
                .build());
    }

    @Test
    void testUpdateThread() {
        // Setup
        final PatchThreadPayload patchThreadPayload = new PatchThreadPayload(ThreadStatus.OPEN);
        final Thread expectedResult = Thread.builder()
                .sender(ConnectProfile.builder()
                        .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                        .build())
                .recipient(ConnectProfile.builder()
                        .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                        .build())
                .bid(Bid.builder()
                        .owner(ConnectProfile.builder()
                                .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                .build())
                        .build())
                .lastMessageAt(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC))
                .lastMessage(Message.builder()
                        .text("message")
                        .media(Media.builder().build())
                        .read(false)
                        .keywords(Set.of("value"))
                        .build())
                .messages(Set.of(Message.builder()
                        .text("message")
                        .media(Media.builder().build())
                        .read(false)
                        .keywords(Set.of("value"))
                        .build()))
                .status(ThreadStatus.OPEN)
                .build();

        // Configure ThreadRepo.findById(...).
        final Optional<Thread> thread = Optional.of(Thread.builder()
                .sender(ConnectProfile.builder()
                        .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                        .build())
                .recipient(ConnectProfile.builder()
                        .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                        .build())
                .bid(Bid.builder()
                        .owner(ConnectProfile.builder()
                                .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                .build())
                        .build())
                .lastMessageAt(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC))
                .lastMessage(Message.builder()
                        .text("message")
                        .media(Media.builder().build())
                        .read(false)
                        .keywords(Set.of("value"))
                        .build())
                .messages(Set.of(Message.builder()
                        .text("message")
                        .media(Media.builder().build())
                        .read(false)
                        .keywords(Set.of("value"))
                        .build()))
                .status(ThreadStatus.OPEN)
                .build());
        when(mockThreadRepo.findById(UUID.fromString("a0d78794-fdc2-4341-8b56-5722147e8b32"))).thenReturn(thread);

        // Configure ThreadRepo.save(...).
        final Thread thread1 = Thread.builder()
                .sender(ConnectProfile.builder()
                        .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                        .build())
                .recipient(ConnectProfile.builder()
                        .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                        .build())
                .bid(Bid.builder()
                        .owner(ConnectProfile.builder()
                                .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                .build())
                        .build())
                .lastMessageAt(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC))
                .lastMessage(Message.builder()
                        .text("message")
                        .media(Media.builder().build())
                        .read(false)
                        .keywords(Set.of("value"))
                        .build())
                .messages(Set.of(Message.builder()
                        .text("message")
                        .media(Media.builder().build())
                        .read(false)
                        .keywords(Set.of("value"))
                        .build()))
                .status(ThreadStatus.OPEN)
                .build();
        when(mockThreadRepo.save(Thread.builder()
                .sender(ConnectProfile.builder()
                        .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                        .build())
                .recipient(ConnectProfile.builder()
                        .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                        .build())
                .bid(Bid.builder()
                        .owner(ConnectProfile.builder()
                                .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                .build())
                        .build())
                .lastMessageAt(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC))
                .lastMessage(Message.builder()
                        .text("message")
                        .media(Media.builder().build())
                        .read(false)
                        .keywords(Set.of("value"))
                        .build())
                .messages(Set.of(Message.builder()
                        .text("message")
                        .media(Media.builder().build())
                        .read(false)
                        .keywords(Set.of("value"))
                        .build()))
                .status(ThreadStatus.OPEN)
                .build())).thenReturn(thread1);

        // Run the test
        final Thread result = messageServiceUnderTest.updateThread(
                UUID.fromString("a0d78794-fdc2-4341-8b56-5722147e8b32"), patchThreadPayload);

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testUpdateThread_ThreadRepoFindByIdReturnsAbsent() {
        // Setup
        final PatchThreadPayload patchThreadPayload = new PatchThreadPayload(ThreadStatus.OPEN);
        when(mockThreadRepo.findById(UUID.fromString("a0d78794-fdc2-4341-8b56-5722147e8b32")))
                .thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(
                () -> messageServiceUnderTest.updateThread(UUID.fromString("a0d78794-fdc2-4341-8b56-5722147e8b32"),
                        patchThreadPayload)).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void testUpdateThread_ThreadRepoSaveThrowsOptimisticLockingFailureException() {
        // Setup
        final PatchThreadPayload patchThreadPayload = new PatchThreadPayload(ThreadStatus.OPEN);

        // Configure ThreadRepo.findById(...).
        final Optional<Thread> thread = Optional.of(Thread.builder()
                .sender(ConnectProfile.builder()
                        .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                        .build())
                .recipient(ConnectProfile.builder()
                        .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                        .build())
                .bid(Bid.builder()
                        .owner(ConnectProfile.builder()
                                .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                .build())
                        .build())
                .lastMessageAt(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC))
                .lastMessage(Message.builder()
                        .text("message")
                        .media(Media.builder().build())
                        .read(false)
                        .keywords(Set.of("value"))
                        .build())
                .messages(Set.of(Message.builder()
                        .text("message")
                        .media(Media.builder().build())
                        .read(false)
                        .keywords(Set.of("value"))
                        .build()))
                .status(ThreadStatus.OPEN)
                .build());
        when(mockThreadRepo.findById(UUID.fromString("a0d78794-fdc2-4341-8b56-5722147e8b32"))).thenReturn(thread);

        when(mockThreadRepo.save(Thread.builder()
                .sender(ConnectProfile.builder()
                        .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                        .build())
                .recipient(ConnectProfile.builder()
                        .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                        .build())
                .bid(Bid.builder()
                        .owner(ConnectProfile.builder()
                                .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                .build())
                        .build())
                .lastMessageAt(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC))
                .lastMessage(Message.builder()
                        .text("message")
                        .media(Media.builder().build())
                        .read(false)
                        .keywords(Set.of("value"))
                        .build())
                .messages(Set.of(Message.builder()
                        .text("message")
                        .media(Media.builder().build())
                        .read(false)
                        .keywords(Set.of("value"))
                        .build()))
                .status(ThreadStatus.OPEN)
                .build())).thenThrow(OptimisticLockingFailureException.class);

        // Run the test
        assertThatThrownBy(
                () -> messageServiceUnderTest.updateThread(UUID.fromString("a0d78794-fdc2-4341-8b56-5722147e8b32"),
                        patchThreadPayload)).isInstanceOf(OptimisticLockingFailureException.class);
    }

    @Test
    void testUpdate() {
        // Setup
        final MessagePatchPayload messagePatchPayload = new MessagePatchPayload("text", false);
        final Message expectedResult = Message.builder()
                .thread(Thread.builder()
                        .sender(ConnectProfile.builder()
                                .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                .build())
                        .recipient(ConnectProfile.builder()
                                .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                .build())
                        .bid(Bid.builder()
                                .owner(ConnectProfile.builder()
                                        .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                        .build())
                                .build())
                        .lastMessageAt(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC))
                        .messages(Set.of())
                        .status(ThreadStatus.OPEN)
                        .build())
                .text("message")
                .media(Media.builder().build())
                .read(false)
                .keywords(Set.of("value"))
                .build();

        // Configure MessageRepo.findById(...).
        final Optional<Message> message = Optional.of(Message.builder()
                .thread(Thread.builder()
                        .sender(ConnectProfile.builder()
                                .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                .build())
                        .recipient(ConnectProfile.builder()
                                .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                .build())
                        .bid(Bid.builder()
                                .owner(ConnectProfile.builder()
                                        .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                        .build())
                                .build())
                        .lastMessageAt(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC))
                        .messages(Set.of())
                        .status(ThreadStatus.OPEN)
                        .build())
                .text("message")
                .media(Media.builder().build())
                .read(false)
                .keywords(Set.of("value"))
                .build());
        when(mockMessageRepo.findById(UUID.fromString("44e98fce-e8bb-4a37-9de7-050a9a726d98"))).thenReturn(message);

        when(mockKeywordService.extractFrom("text")).thenReturn(Set.of("value"));

        // Configure MessageRepo.save(...).
        final Message message1 = Message.builder()
                .thread(Thread.builder()
                        .sender(ConnectProfile.builder()
                                .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                .build())
                        .recipient(ConnectProfile.builder()
                                .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                .build())
                        .bid(Bid.builder()
                                .owner(ConnectProfile.builder()
                                        .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                        .build())
                                .build())
                        .lastMessageAt(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC))
                        .messages(Set.of())
                        .status(ThreadStatus.OPEN)
                        .build())
                .text("message")
                .media(Media.builder().build())
                .read(false)
                .keywords(Set.of("value"))
                .build();
        when(mockMessageRepo.save(Message.builder()
                .thread(Thread.builder()
                        .sender(ConnectProfile.builder()
                                .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                .build())
                        .recipient(ConnectProfile.builder()
                                .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                .build())
                        .bid(Bid.builder()
                                .owner(ConnectProfile.builder()
                                        .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                        .build())
                                .build())
                        .lastMessageAt(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC))
                        .messages(Set.of())
                        .status(ThreadStatus.OPEN)
                        .build())
                .text("message")
                .media(Media.builder().build())
                .read(false)
                .keywords(Set.of("value"))
                .build())).thenReturn(message1);

        // Run the test
        final Message result = messageServiceUnderTest.update(UUID.fromString("44e98fce-e8bb-4a37-9de7-050a9a726d98"),
                messagePatchPayload);

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testUpdate_MessageRepoFindByIdReturnsAbsent() {
        // Setup
        final MessagePatchPayload messagePatchPayload = new MessagePatchPayload("text", false);
        when(mockMessageRepo.findById(UUID.fromString("44e98fce-e8bb-4a37-9de7-050a9a726d98")))
                .thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(() -> messageServiceUnderTest.update(UUID.fromString("44e98fce-e8bb-4a37-9de7-050a9a726d98"),
                messagePatchPayload)).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void testUpdate_KeywordServiceReturnsNoItems() {
        // Setup
        final MessagePatchPayload messagePatchPayload = new MessagePatchPayload("text", false);
        final Message expectedResult = Message.builder()
                .thread(Thread.builder()
                        .sender(ConnectProfile.builder()
                                .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                .build())
                        .recipient(ConnectProfile.builder()
                                .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                .build())
                        .bid(Bid.builder()
                                .owner(ConnectProfile.builder()
                                        .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                        .build())
                                .build())
                        .lastMessageAt(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC))
                        .messages(Set.of())
                        .status(ThreadStatus.OPEN)
                        .build())
                .text("message")
                .media(Media.builder().build())
                .read(false)
                .keywords(Set.of("value"))
                .build();

        // Configure MessageRepo.findById(...).
        final Optional<Message> message = Optional.of(Message.builder()
                .thread(Thread.builder()
                        .sender(ConnectProfile.builder()
                                .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                .build())
                        .recipient(ConnectProfile.builder()
                                .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                .build())
                        .bid(Bid.builder()
                                .owner(ConnectProfile.builder()
                                        .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                        .build())
                                .build())
                        .lastMessageAt(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC))
                        .messages(Set.of())
                        .status(ThreadStatus.OPEN)
                        .build())
                .text("message")
                .media(Media.builder().build())
                .read(false)
                .keywords(Set.of("value"))
                .build());
        when(mockMessageRepo.findById(UUID.fromString("44e98fce-e8bb-4a37-9de7-050a9a726d98"))).thenReturn(message);

        when(mockKeywordService.extractFrom("text")).thenReturn(Collections.emptySet());

        // Configure MessageRepo.save(...).
        final Message message1 = Message.builder()
                .thread(Thread.builder()
                        .sender(ConnectProfile.builder()
                                .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                .build())
                        .recipient(ConnectProfile.builder()
                                .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                .build())
                        .bid(Bid.builder()
                                .owner(ConnectProfile.builder()
                                        .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                        .build())
                                .build())
                        .lastMessageAt(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC))
                        .messages(Set.of())
                        .status(ThreadStatus.OPEN)
                        .build())
                .text("message")
                .media(Media.builder().build())
                .read(false)
                .keywords(Set.of("value"))
                .build();
        when(mockMessageRepo.save(Message.builder()
                .thread(Thread.builder()
                        .sender(ConnectProfile.builder()
                                .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                .build())
                        .recipient(ConnectProfile.builder()
                                .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                .build())
                        .bid(Bid.builder()
                                .owner(ConnectProfile.builder()
                                        .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                        .build())
                                .build())
                        .lastMessageAt(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC))
                        .messages(Set.of())
                        .status(ThreadStatus.OPEN)
                        .build())
                .text("message")
                .media(Media.builder().build())
                .read(false)
                .keywords(Set.of("value"))
                .build())).thenReturn(message1);

        // Run the test
        final Message result = messageServiceUnderTest.update(UUID.fromString("44e98fce-e8bb-4a37-9de7-050a9a726d98"),
                messagePatchPayload);

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testUpdate_MessageRepoSaveThrowsOptimisticLockingFailureException() {
        // Setup
        final MessagePatchPayload messagePatchPayload = new MessagePatchPayload("text", false);

        // Configure MessageRepo.findById(...).
        final Optional<Message> message = Optional.of(Message.builder()
                .thread(Thread.builder()
                        .sender(ConnectProfile.builder()
                                .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                .build())
                        .recipient(ConnectProfile.builder()
                                .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                .build())
                        .bid(Bid.builder()
                                .owner(ConnectProfile.builder()
                                        .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                        .build())
                                .build())
                        .lastMessageAt(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC))
                        .messages(Set.of())
                        .status(ThreadStatus.OPEN)
                        .build())
                .text("message")
                .media(Media.builder().build())
                .read(false)
                .keywords(Set.of("value"))
                .build());
        when(mockMessageRepo.findById(UUID.fromString("44e98fce-e8bb-4a37-9de7-050a9a726d98"))).thenReturn(message);

        when(mockKeywordService.extractFrom("text")).thenReturn(Set.of("value"));
        when(mockMessageRepo.save(Message.builder()
                .thread(Thread.builder()
                        .sender(ConnectProfile.builder()
                                .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                .build())
                        .recipient(ConnectProfile.builder()
                                .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                .build())
                        .bid(Bid.builder()
                                .owner(ConnectProfile.builder()
                                        .id(UUID.fromString("a0300836-4b4c-4e97-9a73-a31acb5606d9"))
                                        .build())
                                .build())
                        .lastMessageAt(ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC))
                        .messages(Set.of())
                        .status(ThreadStatus.OPEN)
                        .build())
                .text("message")
                .media(Media.builder().build())
                .read(false)
                .keywords(Set.of("value"))
                .build())).thenThrow(OptimisticLockingFailureException.class);

        // Run the test
        assertThatThrownBy(() -> messageServiceUnderTest.update(UUID.fromString("44e98fce-e8bb-4a37-9de7-050a9a726d98"),
                messagePatchPayload)).isInstanceOf(OptimisticLockingFailureException.class);
    }
}
