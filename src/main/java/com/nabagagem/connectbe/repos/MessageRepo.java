package com.nabagagem.connectbe.repos;

import com.nabagagem.connectbe.entities.Media;
import com.nabagagem.connectbe.entities.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface MessageRepo extends CrudRepository<Message, UUID> {
    @Query("select (count(m) > 0) from Message m where m.id = ?1 and m.audit.createdBy = ?2")
    boolean existsByIdAndCreator(@NonNull UUID id, @NonNull String createdBy);

    @Query("""
                select m from Message m
                    left join fetch m.reactions
                    inner join m.thread t
                where t.id = :threadId
            """)
    List<Message> findFullByThread(UUID threadId);

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

    @Query("""
                select m.id from Message m
                    left join m.keywords k
                    inner join m.thread t
                    where t.id = :threadId
                    and (:invKeywords = true or k in (:keywords))
                group by m.id
                order by m.audit.createdAt desc
            """)
    Page<String> findMessageIdsByThread(UUID threadId, Set<String> keywords, boolean invKeywords, Pageable pageable);

    @Query("""
                select m from Message m
                    left join fetch m.reactions
                    left join fetch m.thread
                    where m.id in (:ids)
            """)
    List<Message> findFullPageByIds(List<String> ids);

    @Query("""
                select m from Message m
                    inner join fetch m.thread t
                        inner join fetch t.lastMessage
                where m.id = :id
            """)
    Optional<Message> findWithThread(UUID id);
}