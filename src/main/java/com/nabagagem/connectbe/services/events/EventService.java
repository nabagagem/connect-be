package com.nabagagem.connectbe.services.events;

import com.nabagagem.connectbe.domain.notification.EventPayload;
import com.nabagagem.connectbe.domain.notification.EventSearchParams;
import com.nabagagem.connectbe.entities.Event;
import com.nabagagem.connectbe.entities.EventMode;
import com.nabagagem.connectbe.entities.EventType;
import com.nabagagem.connectbe.mappers.EventMapper;
import com.nabagagem.connectbe.repos.EventRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
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
                Optional.ofNullable(eventSearchParams.eventMode())
                        .filter(eventModes -> !eventModes.isEmpty())
                        .orElseGet(() -> List.of(EventMode.values())),
                Optional.ofNullable(eventSearchParams.eventType())
                        .filter(eventTypes -> !eventTypes.isEmpty())
                        .orElseGet(() -> List.of(EventType.values())),
                Optional.ofNullable(eventSearchParams.from())
                        .orElseGet(LocalDate::now),
                Optional.ofNullable(eventSearchParams.to())
                        .orElseGet(() -> LocalDate.now().plusYears(1)),
                pageable
        );
    }
}
