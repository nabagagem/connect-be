package com.nabagagem.connectbe.controllers.messages;

import com.nabagagem.connectbe.domain.MessageThread;
import com.nabagagem.connectbe.domain.SendMessageCommand;
import com.nabagagem.connectbe.domain.TextPayload;
import com.nabagagem.connectbe.domain.ThreadMessage;
import com.nabagagem.connectbe.domain.ThreadMessageCommand;
import com.nabagagem.connectbe.entities.Message;
import com.nabagagem.connectbe.services.MessageService;
import com.nabagagem.connectbe.services.SlugService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
public class ThreadController {
    private final MessageService messageService;
    private final ThreadMapper threadMapper;
    private final SlugService slugService;

    @GetMapping("/api/v1/profile/{id}/threads")
    public List<MessageThread> getThreads(@PathVariable String id) {
        return messageService.getThreadsFor(slugService.getProfileIdFrom(id)).stream()
                .map(threadMapper::toDto).collect(Collectors.toList());
    }

    @GetMapping("/api/v1/threads/{threadId}")
    public List<ThreadMessage> getMessages(
            @PathVariable String threadId) {
        return messageService.getMessagesFrom(UUID.fromString(threadId))
                .stream().map(message -> new ThreadMessage(
                        message.getText(),
                        message.getAudit().getCreatedBy(),
                        message.getAudit().getCreatedAt()
                )).collect(Collectors.toList());
    }

    @PostMapping("/api/v1/threads/{threadId}/messages")
    public void create(@PathVariable String threadId,
                       @RequestBody @Valid TextPayload textPayload) {
        messageService.create(new ThreadMessageCommand(threadId, textPayload));
    }

    @PostMapping("/api/v1/threads")
    @ResponseStatus(HttpStatus.CREATED)
    public MessageRef post(@RequestBody @Valid SendMessageCommand sendMessageCommand) {
        Message message = messageService.send(sendMessageCommand);
        return new MessageRef(
                message.getThread().getId().toString(),
                message.getId().toString()
        );
    }

    private record MessageRef(String threadId, String messageId) {
    }
}