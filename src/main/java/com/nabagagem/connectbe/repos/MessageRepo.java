package com.nabagagem.connectbe.repos;

import com.nabagagem.connectbe.domain.profile.ProfileMailPersonalInfo;
import com.nabagagem.connectbe.entities.Media;
import com.nabagagem.connectbe.entities.Message;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@JaversSpringDataAuditable
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
            """)
    Page<UUID> findMessageIdsByThread(UUID threadId, Set<String> keywords, boolean invKeywords, Pageable pageable);

    @Query("""
                select m from Message m
                    left join fetch m.reactions
                    left join fetch m.thread
                    where m.id in (:ids)
            """)
    List<Message> findFullPageByIds(List<UUID> ids, Sort sort);

    @Query("""
                select m from Message m
                    left join fetch m.reactions
                    inner join fetch m.thread t
                        left join fetch m.media
                        inner join fetch t.sender
                        inner join fetch t.recipient
                        inner join fetch t.lastMessage
                where m.id = :id
            """)
    Optional<Message> findWithThread(UUID id);

    @Query(nativeQuery = true, value = """
                WITH target_message AS
                     (SELECT *
                      FROM message m
                      WHERE m.id = :messageId),
                 newer AS
                     (SELECT m.*
                      FROM message m
                        INNER JOIN target_message ON m.thread_id = target_message.thread_id
                          AND m.created_at >= target_message.created_at
                      ORDER BY m.created_at
                      LIMIT :inFront),
                 older AS (
                     (SELECT m.*
                      FROM message m
                        INNER JOIN target_message ON m.thread_id = target_message.thread_id
                          AND m.created_at <= target_message.created_at
                      ORDER BY m.created_at DESC
                      LIMIT :behind))
            SELECT r.id
            FROM
                (SELECT *
                 FROM older
                 UNION SELECT *
                 FROM newer) AS r
            ORDER BY r.created_at DESC
            """)
    List<UUID> findMessagePage(UUID messageId, Integer behind, Integer inFront);

    @Query("""
                select m from Message m
                    where m.thread.id = :threadId
                    and m.id <> :lastMessageId
                    order by m.audit.createdAt desc
                    limit 1
            """)
    Optional<Message> findPreviousOf(UUID threadId, UUID lastMessageId);

    @Query("""
                select p from ConnectProfile p
                    inner join Thread t
                           inner join t.messages m
                      on t.recipient = p
                    left join UserMailNotification umn
                      on p = umn.profile
                where m.read = false
                and   cast(m.audit.createdBy as uuid) <> p.id
                and   m.audit.createdAt < :time
                and   (umn.sentAt is null or umn.sentAt < m.audit.createdAt)
                and   p.personalInfo.email is not null
                and   p.personalInfo.enableMessageEmail = true
                group by p
            """)
    Set<ProfileMailPersonalInfo> findProfilesWithUnreadMessages(ZonedDateTime time);
}