package com.nabagagem.connectbe.repos;

import com.nabagagem.connectbe.entities.Rating;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

@JaversSpringDataAuditable
public interface RatingRepository extends CrudRepository<Rating, UUID> {
    Long countByTargetProfileId(UUID id);

    @Query("""
                select r from Rating r
                    inner join r.sourceProfile source
                where r.targetProfile.id = :profileId
                order by r.audit.createdAt desc
            """)
    Page<Rating> findRatingsForProfile(UUID profileId, Pageable pageable);

    @Query("""
                select avg(r.stars) from Rating r
                where r.targetProfile.id = :profileId
            """)
    Double findAverageFor(UUID profileId);

    @Query("""
                select r from Rating r
                    where r.targetProfile.id = :targetUserId
                      and r.sourceProfile.id = :loggedUserId
            """)
    Optional<Rating> findFromTo(UUID loggedUserId, UUID targetUserId);
}