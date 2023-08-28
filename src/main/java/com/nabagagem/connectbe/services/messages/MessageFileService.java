package com.nabagagem.connectbe.services.messages;

import com.nabagagem.connectbe.domain.messages.CreateAudioCommand;
import com.nabagagem.connectbe.domain.messages.CreateMpAudioCommand;
import com.nabagagem.connectbe.domain.profile.CreateMessageFileCommand;
import com.nabagagem.connectbe.entities.Media;
import com.nabagagem.connectbe.entities.Message;
import com.nabagagem.connectbe.entities.MessageType;
import com.nabagagem.connectbe.repos.MessageRepo;
import com.nabagagem.connectbe.repos.ThreadRepo;
import com.nabagagem.connectbe.services.MediaService;
import com.nabagagem.connectbe.services.notifications.PublishNotification;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional
public class MessageFileService {
    private final MediaService mediaService;
    private final MessageService messageService;
    private final ThreadRepo threadRepo;
    private final MessageRepo messageRepo;

    @PublishNotification
    public Message create(@Valid CreateMessageFileCommand createMessageFileCommand) {
        return messageService.createMessage(
                Message.builder()
                        .media(
                                Optional.ofNullable(createMessageFileCommand.file())
                                        .map(mediaService::upload)
                                        .orElse(null)
                        )
                        .messageType(
                                Optional.ofNullable(createMessageFileCommand.file())
                                        .map(__ -> MessageType.FILE)
                                        .orElse(MessageType.TEXT)
                        )
                        .text(createMessageFileCommand.text())
                        .thread(threadRepo.findById(createMessageFileCommand.threadId()).orElseThrow())
                        .build()
        );
    }

    public Optional<Media> getPicFor(UUID id) {
        return messageRepo.findMediaFor(id);
    }

    @PublishNotification
    public Message create(@Valid CreateAudioCommand createAudioCommand) {
        return messageService.createMessage(
                Message.builder()
                        .media(
                                Optional.ofNullable(createAudioCommand.audioPayload())
                                        .map(AudioMultipartFile::new)
                                        .map(mediaService::upload)
                                        .orElse(null)
                        )
                        .messageType(MessageType.AUDIO)
                        .thread(threadRepo.findById(createAudioCommand.threadId()).orElseThrow())
                        .build()
        );
    }

    @PublishNotification
    public Message create(CreateMpAudioCommand createMpAudioCommand) {
        return messageService.createMessage(
                Message.builder()
                        .media(
                                mediaService.upload(createMpAudioCommand.file())
                        )
                        .messageType(MessageType.AUDIO)
                        .thread(threadRepo.findById(createMpAudioCommand.threadId()).orElseThrow())
                        .build()
        );

    }
}
