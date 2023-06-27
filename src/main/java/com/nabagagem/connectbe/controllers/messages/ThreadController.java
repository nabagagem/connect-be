package com.nabagagem.connectbe.controllers.messages;

import com.nabagagem.connectbe.domain.*;
import com.nabagagem.connectbe.entities.Message;
import com.nabagagem.connectbe.services.MessageService;
import com.nabagagem.connectbe.services.ThreadAuthService;
import com.nabagagem.connectbe.services.profile.SlugService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RestController
@AllArgsConstructor
public class ThreadController implements MessageMediaUrlTrait {
    private final MessageService messageService;
    private final ThreadMapper threadMapper;
    private final SlugService slugService;
    private final ThreadAuthService threadAuthService;

    @GetMapping("/api/v1/profile/{id}/threads")
    @PreAuthorize("authentication.name == #id")
    public List<MessageThread> getThreads(@PathVariable String id) {
        return messageService.getThreadsFor(slugService.getProfileIdFrom(id)).stream()
                .map(threadMapper::toDto).collect(Collectors.toList());
    }

    @GetMapping("/api/v1/threads/{threadId}")
    public List<ThreadMessage> getMessages(
            @PathVariable UUID threadId) {
        threadAuthService.failIfUnableToRead(threadId);
        return messageService.getMessagesFrom(threadId)
                .stream().map(message -> new ThreadMessage(
                        message.getId(),
                        message.getText(),
                        message.getAudit().getCreatedBy(),
                        getUrlFrom(message),
                        message.getAudit().getCreatedAt()
                )).collect(Collectors.toList());
    }

    @PatchMapping("/api/v1/threads/{threadId}")
    public void patch(@PathVariable UUID threadId,
                      @RequestBody @Valid PatchThreadPayload patchThreadPayload) {
        threadAuthService.failIfUnableToUpdate(threadId, patchThreadPayload);
        messageService.updateThread(threadId, patchThreadPayload);
    }

    @DeleteMapping("/api/v1/threads/{threadId}")
    public void delete(@PathVariable UUID threadId) {
        threadAuthService.failIfUnableToDelete(threadId);
        messageService.deleteThread(threadId);
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
