package com.nabagagem.connectbe.services;

import com.nabagagem.connectbe.domain.EventPicCommand;
import com.nabagagem.connectbe.domain.exceptions.EventNotFoundException;
import com.nabagagem.connectbe.entities.Media;
import com.nabagagem.connectbe.repos.EventRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
public class EventPicService {
    private final EventRepository eventRepository;
    private final MediaService mediaService;

    public void save(EventPicCommand eventPicCommand) {
        eventRepository.findById(eventPicCommand.id())
                .map(event -> {
                    event.setEventPicture(mediaService.upload(eventPicCommand.file()));
                    return event;
                }).ifPresentOrElse(
                        eventRepository::save,
                        () -> {
                            throw new EventNotFoundException();
                        });
    }

    public Optional<Media> getPicFor(UUID id) {
        return eventRepository.findPicFor(id);
    }
}
