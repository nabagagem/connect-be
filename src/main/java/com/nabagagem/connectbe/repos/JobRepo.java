package com.nabagagem.connectbe.repos;

import com.nabagagem.connectbe.domain.JobCategory;
import com.nabagagem.connectbe.domain.JobFrequency;
import com.nabagagem.connectbe.domain.JobMode;
import com.nabagagem.connectbe.domain.JobRequiredAvailability;
import com.nabagagem.connectbe.domain.JobSize;
import com.nabagagem.connectbe.domain.JobStatus;
import com.nabagagem.connectbe.entities.Job;
import org.springframework.data.domain.Page;
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
                    left join j.keywords k
                where (j.jobCategory in (:jobCategories))
                and   (j.jobSize in (:jobSizes))
                and   (j.jobFrequency in (:jobFrequencies))
                and   (j.jobMode in (:jobModes))
                and   (j.requiredAvailability in (:requiredAvailabilities))
                and   (:invSkills = true or skill.name in (:requiredSkills))
                and   (j.owner.id = :owner or cast(:owner as uuid) is null)
                and   (j.requiredDates.startAt >= :startAt or cast(:startAt as timestamp) is null)
                and   (j.requiredDates.finishAt <= :finishAt or cast(:finishAt as timestamp) is null)
                and   (:invKeywords = true or k in (:keywords))
                and   (j.owner.id <> :loggedUserId)
                and   (j.owner.parentProfile is null or j.owner.parentProfile.id <> :loggedUserId)
                group by j.id
            """)
    Page<UUID> findIdsBy(Set<JobCategory> jobCategories,
                         Set<JobSize> jobSizes,
                         Set<JobFrequency> jobFrequencies,
                         Set<JobMode> jobModes,
                         Set<JobRequiredAvailability> requiredAvailabilities,
                         Set<String> requiredSkills,
                         Boolean invSkills,
                         UUID owner,
                         ZonedDateTime startAt,
                         ZonedDateTime finishAt,
                         Set<String> keywords,
                         Boolean invKeywords,
                         UUID loggedUserId, Pageable pageable);

    @Query("""
                select j.id from Job j
                    left join j.keywords k
                where (j.jobCategory = :jobCategory or cast(:jobCategory as char) is null )
                and (j.jobStatus = :jobStatus or cast(:jobStatus as char) is null)
                and (j.jobMode = :jobMode or cast(:jobMode as char) is null)
                and (:invKeywords = true or k in (:keywords))
                and (j.owner.id = :loggedUserId)
                group by j.id
            """)
    Page<UUID> findIdsBy(JobCategory jobCategory,
                         JobStatus jobStatus,
                         JobMode jobMode,
                         Set<String> keywords,
                         Boolean invKeywords,
                         UUID loggedUserId,
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