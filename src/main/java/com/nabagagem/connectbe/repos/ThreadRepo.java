package com.nabagagem.connectbe.repos;

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
                    left join t.bid b
                where ((s.id = :senderId and r.id = :recipientId)    
                           or (r.id = :senderId and s.id = :recipientId))
                and (b.id = :bidId or (cast(:bidId as uuid) is null))
            """)
    Optional<Thread> findByProfile(UUID recipientId, UUID senderId, UUID bidId);

    @Query("""
                select t from Thread t
                    inner join fetch t.recipient r
                    inner join fetch t.sender s
                    left join fetch t.lastMessage
                where r.id = :id
                or s.id = :id
                order by t.lastMessageAt desc
            """)
    List<Thread> findFor(UUID id);

    @Query("""
                select t from Thread t
                    left join fetch t.messages
                    inner join fetch t.sender
                    inner join fetch t.recipient
                where (t.sender.id = :userId or t.recipient.id = :userId)
                and   (t.bid.id = :bidId)
            """)
    Optional<Thread> findBy(UUID bidId, UUID userId);
    
    @Query("""
                select case when count(t)>0 then true else false end
                    from Thread t
                    where (t.recipient.id = :loggedUserId or t.sender.id = :loggedUserId)
                    and t.id = :threadId
                    
            """)
    boolean existsByIdAndUsers(UUID threadId, UUID loggedUserId);
}