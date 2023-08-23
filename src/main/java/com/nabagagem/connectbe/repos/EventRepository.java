package com.nabagagem.connectbe.repos;

import com.nabagagem.connectbe.entities.Event;
import com.nabagagem.connectbe.entities.EventMode;
import com.nabagagem.connectbe.entities.EventType;
import com.nabagagem.connectbe.entities.Media;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@JaversSpringDataAuditable
public interface EventRepository extends CrudRepository<Event, UUID>, PagingAndSortingRepository<Event, UUID> {

    @Query("""
                select e from Event e
                    where (e.eventMode in (:eventModes))
                    and   (e.eventType in (:eventTypes))
                    and   (e.eventDate between :from and :to)
                order by e.eventDate
            """)
    Page<Event> listBy(List<EventMode> eventModes,
                       List<EventType> eventTypes,
                       LocalDate from,
                       LocalDate to, Pageable pageable);

    @Query("""
                select e.eventPicture from Event e
                where e.id = :id
            """)
    Optional<Media> findPicFor(UUID id);
}