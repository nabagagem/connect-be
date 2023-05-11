package com.nabagagem.connectbe.resources;

import com.nabagagem.connectbe.entities.Event;
import com.nabagagem.connectbe.entities.EventMode;
import com.nabagagem.connectbe.entities.EventType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface EventRepository extends CrudRepository<Event, UUID>, PagingAndSortingRepository<Event, UUID> {

    @Query("""
                select e from Event e
                    where (e.eventMode in (:eventModes))
                    and   (e.eventType in (:eventTypes))
                    and   (e.eventDate between :from and :to)
            """)
    Page<Event> listBy(List<EventMode> eventModes,
                       List<EventType> eventTypes,
                       LocalDate from,
                       LocalDate to, Pageable pageable);
}