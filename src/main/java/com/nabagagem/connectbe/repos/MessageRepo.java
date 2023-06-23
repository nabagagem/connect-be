package com.nabagagem.connectbe.repos;

import com.nabagagem.connectbe.entities.Media;
import com.nabagagem.connectbe.entities.Message;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepo extends CrudRepository<Message, UUID> {

    List<Message> findByThreadId(UUID threadId);

    @Query("""
                select m.media from Message m
                where m.id = :id
            """)
    Optional<Media> findMediaFor(UUID id);
}