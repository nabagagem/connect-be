package com.nabagagem.connectbe.services;

import com.nabagagem.connectbe.domain.MessagePatchPayload;
import com.nabagagem.connectbe.domain.PatchThreadPayload;
import com.nabagagem.connectbe.domain.SendMessageCommand;
import com.nabagagem.connectbe.domain.SendMessagePayload;
import com.nabagagem.connectbe.domain.ThreadMessageCommand;
import com.nabagagem.connectbe.domain.exceptions.BadRequestException;
import com.nabagagem.connectbe.domain.exceptions.ErrorType;
import com.nabagagem.connectbe.entities.Bid;
import com.nabagagem.connectbe.entities.ConnectProfile;
import com.nabagagem.connectbe.entities.Message;
import com.nabagagem.connectbe.entities.ProfileThreadItem;
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
                .orElseGet(() -> profileRepo.findParentFrom(sendMessagePayload.getRecipientId()).orElseThrow());
        UUID senderId = sendMessageCommand.getSenderId();
        return threadRepo.findByProfile(
                        recipient.getId(), senderId, bidId
                )
                .map(t -> {
                    t.setLastMessageAt(ZonedDateTime.now());
                    return t;
                }).orElseGet(() -> Thread.builder()
                        .sender(profileRepo.findParentFrom(senderId)
                                .orElseThrow())
                        .recipient(recipient)
                        .bid(bidOptional.orElse(null))
                        .lastMessageAt(ZonedDateTime.now())
                        .build());
    }

    public List<ProfileThreadItem> getThreadsFor(UUID id) {
        return threadRepo.findThreadsFor(id, id.toString());
    }

    public List<Message> getMessagesFrom(UUID threadId) {
        return messageRepo.findFullByThread(threadId);
    }

    @PublishNotification
    public Thread create(ThreadMessageCommand threadMessageCommand) {
        Thread thread = threadRepo.findById(threadMessageCommand.threadId())
                .orElseThrow();
        validate(thread);
        Message message = messageRepo.save(Message.builder()
                .text(threadMessageCommand.textPayload().text())
                .thread(thread)
                .build());
        thread.setLastMessage(message);
        return threadRepo.save(thread);
    }

    public Message delete(UUID id) {
        Message message = messageRepo.findById(id).orElseThrow();
        return deleteMessage(message);
    }

    private Message deleteMessage(Message message) {
        Optional.ofNullable(message.getMedia())
                .ifPresent(mediaService::delete);
        messageRepo.delete(message);
        return message;
    }

    public Thread deleteThread(UUID threadId) {
        Thread thread = threadRepo.findById(threadId).orElseThrow();
        thread.getMessages()
                .forEach(this::deleteMessage);
        threadRepo.delete(thread);
        return thread;
    }

    public Thread updateThread(UUID threadId, PatchThreadPayload patchThreadPayload) {
        Thread thread = threadRepo.findById(threadId).orElseThrow();
        thread.setStatus(patchThreadPayload.status());
        return threadRepo.save(thread);
    }

    public Message update(UUID id, MessagePatchPayload messagePatchPayload) {
        Message message = messageRepo.findById(id).orElseThrow();
        Optional.ofNullable(messagePatchPayload.text())
                .ifPresent(message::setText);
        Optional.ofNullable(messagePatchPayload.read())
                .ifPresent(message::setRead);
        return messageRepo.save(message);
    }
}
