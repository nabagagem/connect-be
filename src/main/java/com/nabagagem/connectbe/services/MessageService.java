package com.nabagagem.connectbe.services;

import com.nabagagem.connectbe.domain.SendMessageCommand;
import com.nabagagem.connectbe.domain.ThreadMessageCommand;
import com.nabagagem.connectbe.entities.Message;
import com.nabagagem.connectbe.entities.Thread;
import com.nabagagem.connectbe.resources.MessageRepo;
import com.nabagagem.connectbe.resources.ProfileRepo;
import com.nabagagem.connectbe.resources.ThreadRepo;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional
public class MessageService {
    private final MessageRepo messageRepo;
    private final ProfileRepo profileRepo;
    private final ThreadRepo threadRepo;

    public Message send(SendMessageCommand sendMessageCommand) {
        Thread thread = threadRepo.save(findOrInitThread(sendMessageCommand));
        Message message = messageRepo.save(Message.builder()
                .text(sendMessageCommand.sendMessagePayload().message())
                .thread(thread)
                .build());
        thread.setLastMessage(message);
        threadRepo.save(thread);
        return message;
    }

    private Thread findOrInitThread(SendMessageCommand sendMessageCommand) {
        UUID recipientId = UUID.fromString(sendMessageCommand.sendMessagePayload().recipientId());
        UUID senderId = UUID.fromString(sendMessageCommand.senderId());
        return threadRepo.findByProfile(
                        recipientId,
                        senderId)
                .map(t -> {
                    t.setLastMessageAt(LocalDateTime.now());
                    return t;
                }).orElseGet(() -> Thread.builder()
                        .sender(profileRepo.findById(senderId)
                                .orElseThrow())
                        .recipient(profileRepo.findById(recipientId)
                                .orElseThrow())
                        .lastMessageAt(LocalDateTime.now())
                        .build());
    }

    public List<Thread> getThreadsFor(UUID id) {
        return threadRepo.findFor(id);
    }

    public List<Message> getMessagesFrom(UUID threadId) {
        return messageRepo.findByThreadId(threadId);
    }

    public void create(ThreadMessageCommand threadMessageCommand) {
        Thread thread = threadRepo.findById(UUID.fromString(threadMessageCommand.threadId()))
                .orElseThrow();
        Message message = messageRepo.save(Message.builder()
                .text(threadMessageCommand.textPayload().text())
                .thread(thread)
                .build());
        thread.setLastMessage(message);
        threadRepo.save(thread);
    }
}
