package com.nabagagem.connectbe.controllers.messages;

import com.nabagagem.connectbe.controllers.MediaControllerHelper;
import com.nabagagem.connectbe.domain.ResourceRef;
import com.nabagagem.connectbe.domain.messages.AudioPayload;
import com.nabagagem.connectbe.domain.messages.CreateAudioCommand;
import com.nabagagem.connectbe.domain.messages.MessagePatchPayload;
import com.nabagagem.connectbe.domain.profile.CreateMessageFileCommand;
import com.nabagagem.connectbe.services.messages.MessageAuthService;
import com.nabagagem.connectbe.services.messages.MessageFileService;
import com.nabagagem.connectbe.services.messages.MessageService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Slf4j
@RestController
@AllArgsConstructor
public class MessageController {
    private final MessageService messageService;
    private final MessageFileService messageFileService;
    private final MediaControllerHelper mediaControllerHelper;
    private final MessageAuthService messageAuthService;

    @PostMapping(value = "/api/v1/threads/{threadId}/files",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResourceRef upload(@RequestParam(required = false) MultipartFile file,
                              @RequestParam(required = false) String text,
                              @PathVariable UUID threadId) {
        log.info("Request body: {}", text);
        messageAuthService.failIfUnableToWriteOnThread(threadId);
        return new ResourceRef(messageFileService.create(
                new CreateMessageFileCommand(file, text, threadId)
        ).getId());
    }

    @PostMapping(value = "/api/v1/threads/{threadId}/audios", consumes = MediaType.TEXT_PLAIN_VALUE)
    public void uploadAudio(@PathVariable UUID threadId,
                            @RequestBody String body) {
        messageAuthService.failIfUnableToWriteOnThread(threadId);
        messageFileService.create(new CreateAudioCommand(threadId, new AudioPayload(body)));
    }

    @DeleteMapping("/api/v1/messages/{id}")
    public void delete(@PathVariable UUID id) {
        messageAuthService.failIfUnableToDelete(id);
        messageService.delete(id);
    }

    @PatchMapping("/api/v1/messages/{id}")
    public void update(@PathVariable UUID id,
                       @RequestBody @Valid MessagePatchPayload messagePatchPayload) {
        messageAuthService.failIfUnableToPatch(id, messagePatchPayload);
        messageService.update(id, messagePatchPayload);
    }

    @GetMapping("/api/v1/messages/{id}/file")
    public ResponseEntity<byte[]> get(@PathVariable UUID id) {
        log.info("Retrieving message file: {}", id);
        messageAuthService.failIfUnableToRead(id);
        return messageFileService.getPicFor(id)
                .map(mediaControllerHelper::toResponse)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
