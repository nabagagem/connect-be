package com.nabagagem.connectbe.controllers.messages;

import com.nabagagem.connectbe.controllers.MediaControllerHelper;
import com.nabagagem.connectbe.domain.CreateMessageFileCommand;
import com.nabagagem.connectbe.domain.ResourceRef;
import com.nabagagem.connectbe.services.MessageFileService;
import com.nabagagem.connectbe.services.MessageService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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

    @PostMapping(value = "/api/v1/threads/{threadId}/files",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResourceRef upload(@RequestParam MultipartFile file,
                              @PathVariable UUID threadId) {
        return messageFileService.create(
                new CreateMessageFileCommand(file, threadId)
        );
    }

    @DeleteMapping("/api/v1/messages/{id}")
    public void delete(@PathVariable UUID id) {
        messageService.delete(id);
    }

    @GetMapping("/api/v1/messages/{id}/file")
    public ResponseEntity<byte[]> get(@PathVariable UUID id) {
        log.info("Retrieving user picture: {}", id);
        return messageFileService.getPicFor(id)
                .map(mediaControllerHelper::toResponse)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}