package com.nabagagem.connectbe.controllers.messages;

import com.nabagagem.connectbe.domain.PatchThreadPayload;
import com.nabagagem.connectbe.domain.SendMessageCommand;
import com.nabagagem.connectbe.domain.TextPayload;
import com.nabagagem.connectbe.domain.ThreadMessage;
import com.nabagagem.connectbe.domain.ThreadMessageCommand;
import com.nabagagem.connectbe.entities.Message;
import com.nabagagem.connectbe.entities.ProfileThreadItem;
import com.nabagagem.connectbe.services.MessageService;
import com.nabagagem.connectbe.services.ThreadAuthService;
import com.nabagagem.connectbe.services.mappers.MessageMapper;
import com.nabagagem.connectbe.services.profile.SlugService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RestController
@AllArgsConstructor
public class ThreadController implements MessageMediaUrlTrait {
    private final MessageService messageService;
    private final SlugService slugService;
    private final ThreadAuthService threadAuthService;
    private final MessageMapper messageMapper;

    @GetMapping("/api/v1/profile/{id}/threads")
    @PreAuthorize("authentication.name == #id")
    public List<ProfileThreadItem> getThreads(@PathVariable String id) {
        return messageService.getThreadsFor(slugService.getProfileIdFrom(id));
    }

    @GetMapping("/api/v1/threads/{threadId}")
    public List<ThreadMessage> getMessages(
            @PathVariable UUID threadId) {
        threadAuthService.failIfUnableToRead(threadId);
        return messageService.getMessagesFrom(threadId)
                .stream().map(messageMapper::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/api/v2/threads/{threadId}")
    public Page<ThreadMessage> getPage(
            @PathVariable UUID threadId,
            Pageable pageable) {
        threadAuthService.failIfUnableToRead(threadId);
        return messageService.getMessagesPageFrom(threadId, pageable)
                .map(messageMapper::toDto);
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
    public void create(@PathVariable UUID threadId,
                       @RequestBody @Valid TextPayload textPayload) {
        threadAuthService.failIfUnableToRead(threadId);
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
