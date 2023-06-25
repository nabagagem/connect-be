package com.nabagagem.connectbe.services;

import com.nabagagem.connectbe.domain.PatchThreadPayload;
import com.nabagagem.connectbe.domain.SendMessageCommand;
import com.nabagagem.connectbe.domain.SendMessagePayload;
import com.nabagagem.connectbe.domain.TextPayload;
import com.nabagagem.connectbe.domain.ThreadMessageCommand;
import com.nabagagem.connectbe.domain.exceptions.BadRequestException;
import com.nabagagem.connectbe.domain.exceptions.ErrorType;
import com.nabagagem.connectbe.entities.Bid;
import com.nabagagem.connectbe.entities.ConnectProfile;
import com.nabagagem.connectbe.entities.Message;
import com.nabagagem.connectbe.entities.Thread;
import com.nabagagem.connectbe.repos.BidRepository;
import com.nabagagem.connectbe.repos.MessageRepo;
import com.nabagagem.connectbe.repos.ProfileRepo;
import com.nabagagem.connectbe.repos.ThreadRepo;
import com.nabagagem.connectbe.services.notifications.PublishNotification;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@SuppressWarnings("UnusedReturnValue")
@Service
@AllArgsConstructor
@Transactional
public class MessageService {
    private final MessageRepo messageRepo;
    private final ProfileRepo profileRepo;
    private final ThreadRepo threadRepo;
    private final BidRepository bidRepository;
    private final MediaService mediaService;

    @PublishNotification
    public Message send(SendMessageCommand sendMessageCommand) {
        Thread thread = threadRepo.save(findOrInitThread(sendMessageCommand));
        validate(thread);
        Message message = messageRepo.save(Message.builder()
                .text(sendMessageCommand.getSendMessagePayload().getMessage())
                .thread(thread)
                .build());
        thread.setLastMessage(message);
        threadRepo.save(thread);
        return message;
    }

    private void validate(Thread thread) {
        if (thread.isBlocked()) {
            throw BadRequestException.builder()
                    .errorType(ErrorType.THREAD_BLOCKED)
                    .build();
        }
    }

    Thread findOrInitThread(SendMessageCommand sendMessageCommand) {
        SendMessagePayload sendMessagePayload = sendMessageCommand.getSendMessagePayload();
        UUID bidId = sendMessagePayload.getBidId();
        Optional<Bid> bidOptional = Optional.ofNullable(bidId)
                .flatMap(bidRepository::findById);
        ConnectProfile recipient = bidOptional
                .map(Bid::getOwner)
                .orElseGet(() -> profileRepo.findById(sendMessagePayload.getRecipientId()).orElseThrow());
        UUID senderId = sendMessageCommand.getSenderId();
        return threadRepo.findByProfile(
                        recipient.getId(), senderId, bidId
                )
                .map(t -> {
                    t.setLastMessageAt(ZonedDateTime.now());
                    return t;
                }).orElseGet(() -> Thread.builder()
                        .sender(profileRepo.findById(senderId)
                                .orElseThrow())
                        .recipient(recipient)
                        .bid(bidOptional.orElse(null))
                        .lastMessageAt(ZonedDateTime.now())
                        .build());
    }

    public List<Thread> getThreadsFor(UUID id) {
        return threadRepo.findFor(id);
    }

    public List<Message> getMessagesFrom(UUID threadId) {
        return messageRepo.findByThreadId(threadId);
    }

    @PublishNotification
    public Thread create(ThreadMessageCommand threadMessageCommand) {
        Thread thread = threadRepo.findById(UUID.fromString(threadMessageCommand.threadId()))
                .orElseThrow();
        validate(thread);
        Message message = messageRepo.save(Message.builder()
                .text(threadMessageCommand.textPayload().text())
                .thread(thread)
                .build());
        thread.setLastMessage(message);
        return threadRepo.save(thread);
    }

    @PublishNotification
    public Message delete(UUID id) {
        Message message = messageRepo.findById(id).orElseThrow();
        Optional.ofNullable(message.getMedia())
                .ifPresent(mediaService::delete);
        messageRepo.delete(message);
        return message;
    }

    @PublishNotification
    public Thread deleteThread(UUID threadId) {
        Thread thread = threadRepo.findById(threadId).orElseThrow();
        threadRepo.delete(thread);
        return thread;
    }

    @PublishNotification
    public Thread updateThread(UUID threadId, PatchThreadPayload patchThreadPayload) {
        Thread thread = threadRepo.findById(threadId).orElseThrow();
        thread.setStatus(patchThreadPayload.status());
        return threadRepo.save(thread);
    }

    @PublishNotification
    public Message update(UUID id, TextPayload textPayload) {
        Message message = messageRepo.findById(id).orElseThrow();
        message.setText(textPayload.text());
        return messageRepo.save(message);
    }
}
