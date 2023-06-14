package com.nabagagem.connectbe.repos;

import com.nabagagem.connectbe.domain.*;
import com.nabagagem.connectbe.entities.Job;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface JobRepo extends PagingAndSortingRepository<Job, UUID>,
        CrudRepository<Job, UUID> {

    @Query("""
                select j.id from Job j
                    inner join j.owner
                    left join j.requiredSkills skill
                    left join j.tags tag
                where (j.jobCategory in (:jobCategories))
                and   (j.jobSize in (:jobSizes))
                and   (j.jobFrequency in (:jobFrequencies))
                and   (j.jobMode in (:jobModes))
                and   (j.requiredAvailability in (:requiredAvailabilities))
                and   (skill.name in (:requiredSkills) or (:requiredSkills) is null)
                and   (tag in (:tags) or (:tags) is null)
                and   (j.owner.id = :owner or cast(:owner as uuid) is null)
                and   (j.requiredDates.startAt >= :startAt or cast(:startAt as timestamp) is null)
                and   (j.requiredDates.finishAt <= :finishAt or cast(:finishAt as timestamp) is null)
                group by j.id
            """)
    List<UUID> findIdsBy(Set<JobCategory> jobCategories,
                         Set<JobSize> jobSizes,
                         Set<JobFrequency> jobFrequencies,
                         Set<JobMode> jobModes,
                         Set<JobRequiredAvailability> requiredAvailabilities,
                         Set<String> requiredSkills,
                         Set<String> tags,
                         UUID owner,
                         ZonedDateTime startAt,
                         ZonedDateTime finishAt,
                         Pageable pageable);

    @Query("""
                    select j from Job j
                        inner join fetch j.owner
                        left join fetch j.requiredSkills
                        left join fetch j.tags
                    where j.id in (:ids)
            """)
    List<Job> findAndFetchByIds(List<UUID> ids);

    boolean existsByOwnerIdAndId(UUID uuid, UUID jobId);
}