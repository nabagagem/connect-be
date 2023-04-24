package com.nabagagem.connectbe.resources;

import com.nabagagem.connectbe.entities.Thread;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ThreadRepo extends CrudRepository<Thread, UUID> {
    @Query("""
                select t from Thread t
                    inner join fetch t.sender s
                    inner join fetch t.recipient r
                where (s.id = :senderId and r.id = :recipientId)
                or (r.id = :senderId and s.id = :recipientId)
            """)
    Optional<Thread> findByProfile(UUID recipientId, UUID senderId);

    @Query("""
                select t from Thread t
                    inner join fetch t.recipient r
                    inner join fetch t.sender s
                    left join fetch t.lastMessage
                where t.id = :id
                or s.id = :id
                order by t.lastMessageAt desc
            """)
    List<Thread> findFor(UUID id);
}