package com.nabagagem.connectbe.repos;

import com.nabagagem.connectbe.entities.Media;
import com.nabagagem.connectbe.entities.Message;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepo extends CrudRepository<Message, UUID> {
    @Query("select (count(m) > 0) from Message m where m.id = ?1 and m.audit.createdBy = ?2")
    boolean existsByIdAndCreator(@NonNull UUID id, @NonNull String createdBy);

    List<Message> findByThreadId(UUID threadId);

    @Query("""
                select m.media from Message m
                where m.id = :id
            """)
    Optional<Media> findMediaFor(UUID id);

    @Query("""
                select (count(t)>0)
                    from Thread t
                    inner join t.messages m
                    where (t.sender.id = :loggedUserId or t.recipient.id = :loggedUserId)
                    and m.id = :id
            """)
    boolean isUserOnThread(UUID id, UUID loggedUserId);

    @Query("""
                select count(t)>0 from Thread t
                    inner join t.messages m
                where m.id = :id
                      and (t.recipient.id = :loggedUserId or t.sender.id = :loggedUserId)
                      and (m.audit.createdBy <> :loggedUserIdString)
            """)
    boolean isTheRecipientOf(UUID id, UUID loggedUserId, String loggedUserIdString);
}