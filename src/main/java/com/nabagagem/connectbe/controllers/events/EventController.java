package com.nabagagem.connectbe.controllers.events;

import com.nabagagem.connectbe.domain.EventItemPayload;
import com.nabagagem.connectbe.domain.EventPayload;
import com.nabagagem.connectbe.domain.EventSearchParams;
import com.nabagagem.connectbe.domain.ResourceRef;
import com.nabagagem.connectbe.mappers.EventMapper;
import com.nabagagem.connectbe.services.EventService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/events")
public class EventController {
    private final EventService eventService;
    private final EventMapper eventMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResourceRef post(@RequestBody EventPayload eventPayload) {
        return new ResourceRef(eventService.create(eventPayload).getId().toString());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventPayload> get(@PathVariable UUID id) {
        return eventService.getById(id)
                .map(eventMapper::toDto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        eventService.delete(id);
    }

    @GetMapping
    public Page<EventItemPayload> list(EventSearchParams eventSearchParams,
                                       Pageable pageable) {
        return eventService.listBy(eventSearchParams, pageable)
                .map(eventMapper::toItemDto);
    }
}
