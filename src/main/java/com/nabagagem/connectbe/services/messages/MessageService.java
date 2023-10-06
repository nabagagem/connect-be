package com.nabagagem.connectbe.services.messages;

import com.nabagagem.connectbe.domain.exceptions.BadRequestException;
import com.nabagagem.connectbe.domain.exceptions.ErrorType;
import com.nabagagem.connectbe.domain.messages.MessagePatchPayload;
import com.nabagagem.connectbe.domain.messages.PatchThreadPayload;
import com.nabagagem.connectbe.domain.messages.SendMessageCommand;
import com.nabagagem.connectbe.domain.messages.SendMessagePayload;
import com.nabagagem.connectbe.domain.messages.ThreadMessageCommand;
import com.nabagagem.connectbe.entities.Bid;
import com.nabagagem.connectbe.entities.ConnectProfile;
import com.nabagagem.connectbe.entities.Message;
import com.nabagagem.connectbe.entities.ProfileThreadItem;
import com.nabagagem.connectbe.entities.Thread;
import com.nabagagem.connectbe.repos.BidRepository;
import com.nabagagem.connectbe.repos.MessageRepo;
import com.nabagagem.connectbe.repos.ProfileRepo;
import com.nabagagem.connectbe.repos.ThreadRepo;
import com.nabagagem.connectbe.services.MediaService;
import com.nabagagem.connectbe.services.notifications.PublishNotification;
import com.nabagagem.connectbe.services.search.KeywordService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.nabagagem.connectbe.services.notifications.Action.DELETED;
import static com.nabagagem.connectbe.services.notifications.Action.UPDATED;

@Slf4j
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
    private final KeywordService keywordService;

    @PublishNotification
    public Message send(SendMessageCommand sendMessageCommand) {
        Thread thread = threadRepo.save(findOrInitThread(sendMessageCommand));
        Message message = Message.builder()
                .text(sendMessageCommand.getSendMessagePayload().getMessage())
                .thread(thread)
                .build();
        return createMessage(message);
    }

    Message createMessage(Message newMessage) {
        Thread thread = newMessage.getThread();
        validate(thread);
        Message message = save(newMessage);
        thread.setLastMessage(message);
        save(thread);
        return message;
    }

    private Message save(Message message) {
        message.setKeywords(
                Optional.ofNullable(message.getText())
                        .map(keywordService::extractFrom)
                        .map(HashSet::new)
                        .orElseGet(HashSet::new)
        );
        return messageRepo.save(message);
    }

    private Thread save(Thread thread) {
        return threadRepo.save(thread);
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
        Message message = save(Message.builder()
                .text(threadMessageCommand.textPayload().text())
                .thread(thread)
                .build());
        thread.setLastMessage(message);
        return save(thread);
    }

    @PublishNotification(DELETED)
    public Message delete(UUID id) {
        Message message = messageRepo.findWithThread(id).orElseThrow();
        Thread thread = message.getThread();
        UUID lastMessageId = thread.getLastMessage().getId();
        if (message.getId().equals(lastMessageId)) {
            log.info("Message id {} is the last one of thread {}", message.getId(), thread.getId());
            messageRepo.findPreviousOf(thread.getId(), lastMessageId)
                    .ifPresentOrElse(newLastMessage -> {
                        log.info("There is a message older than {} on thread {}", message.getId(), thread.getId());
                        thread.setLastMessage(newLastMessage);
                        threadRepo.save(thread);
                        deleteMessage(message);
                    }, () -> {
                        log.info("There are no more messages on thread {}", thread.getId());
                        deleteThread(thread);
                    });
            return message;
        } else {
            return deleteMessage(message);
        }
    }

    private Message deleteMessage(Message message) {
        Optional.ofNullable(message.getMedia())
                .ifPresent(mediaService::delete);
        messageRepo.delete(message);
        return message;
    }

    public Thread deleteThread(UUID threadId) {
        Thread thread = threadRepo.findById(threadId).orElseThrow();
        return deleteThread(thread);
    }

    private Thread deleteThread(Thread thread) {
        thread.getMessages()
                .forEach(this::deleteMessage);
        threadRepo.delete(thread);
        return thread;
    }

    public Thread updateThread(UUID threadId, PatchThreadPayload patchThreadPayload) {
        Thread thread = threadRepo.findById(threadId).orElseThrow();
        thread.setStatus(patchThreadPayload.status());
        return save(thread);
    }

    @PublishNotification(UPDATED)
    public Message update(UUID id, MessagePatchPayload messagePatchPayload) {
        Message message = messageRepo.findById(id).orElseThrow();
        Optional.ofNullable(messagePatchPayload.text())
                .ifPresent(text -> {
                    message.setText(text);
                    message.setTextUpdated(true);
                });
        Optional.ofNullable(messagePatchPayload.read())
                .ifPresent(message::setRead);
        return save(message);
    }

    public void deleteForUser(UUID id) {
        threadRepo.findThreadsFor(id, id.toString())
                .stream().map(ProfileThreadItem::getId)
                .forEach(this::deleteThread);
    }
}
