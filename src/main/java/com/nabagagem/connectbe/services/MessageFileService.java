package com.nabagagem.connectbe.services;

import com.nabagagem.connectbe.domain.CreateMessageFileCommand;
import com.nabagagem.connectbe.domain.ResourceRef;
import com.nabagagem.connectbe.entities.Media;
import com.nabagagem.connectbe.entities.Message;
import com.nabagagem.connectbe.repos.MessageRepo;
import com.nabagagem.connectbe.repos.ThreadRepo;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class MessageFileService {
    private final MediaService mediaService;
    private final MessageRepo messageRepo;
    private final ThreadRepo threadRepo;

    public ResourceRef create(@Valid CreateMessageFileCommand createMessageFileCommand) {
        Message message = messageRepo.save(
                Message.builder()
                        .media(
                                Optional.ofNullable(createMessageFileCommand.file())
                                        .map(mediaService::upload)
                                        .orElse(null)
                        )
                        .text(createMessageFileCommand.text())
                        .thread(threadRepo.findById(createMessageFileCommand.threadId()).orElseThrow())
                        .build()
        );
        return new ResourceRef(message.getId().toString());
    }

    public Optional<Media> getPicFor(UUID id) {
        return messageRepo.findMediaFor(id);
    }
}
