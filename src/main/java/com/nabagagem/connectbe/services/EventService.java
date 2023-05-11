package com.nabagagem.connectbe.services;

import com.nabagagem.connectbe.domain.EventPayload;
import com.nabagagem.connectbe.domain.EventSearchParams;
import com.nabagagem.connectbe.entities.Event;
import com.nabagagem.connectbe.mappers.EventMapper;
import com.nabagagem.connectbe.resources.EventRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    public Event create(EventPayload eventPayload) {
        return eventRepository.save(eventMapper.toEntity(eventPayload));
    }

    public Optional<Event> getById(UUID id) {
        return eventRepository.findById(id);
    }

    public void delete(UUID id) {
        eventRepository.deleteById(id);
    }

    public Page<Event> listBy(EventSearchParams eventSearchParams, Pageable pageable) {
        return eventRepository.listBy(
                eventSearchParams.eventMode(),
                eventSearchParams.eventType(),
                eventSearchParams.from(),
                eventSearchParams.to(),
                pageable
        );
    }
}
