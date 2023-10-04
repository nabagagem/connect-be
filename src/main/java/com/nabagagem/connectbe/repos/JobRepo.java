package com.nabagagem.connectbe.repos;

import com.nabagagem.connectbe.domain.job.JobCategory;
import com.nabagagem.connectbe.domain.job.JobFrequency;
import com.nabagagem.connectbe.domain.job.JobRequiredAvailability;
import com.nabagagem.connectbe.domain.job.JobSearchInfo;
import com.nabagagem.connectbe.domain.job.JobSearchParams;
import com.nabagagem.connectbe.domain.job.JobSize;
import com.nabagagem.connectbe.domain.job.JobStatus;
import com.nabagagem.connectbe.domain.profile.WorkingMode;
import com.nabagagem.connectbe.entities.Job;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@JaversSpringDataAuditable
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
                AND   (j.jobStatus = 'PUBLISHED')
                group by j.id
            """)
    Page<UUID> findIdsBy(Set<JobCategory> jobCategories,
                         Set<JobSize> jobSizes,
                         Set<JobFrequency> jobFrequencies,
                         Set<WorkingMode> jobModes,
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
                         WorkingMode jobMode,
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

    default Page<UUID> findIdsBy(JobSearchParams jobSearchParams, Set<String> keywords,
                                 UUID loggedUserId, Pageable pageable) {
        return findIdsBy(
                emptyOrFull(jobSearchParams.jobCategories(), JobCategory.values()),
                emptyOrFull(jobSearchParams.jobSize(), JobSize.values()),
                emptyOrFull(jobSearchParams.jobFrequencies(), JobFrequency.values()),
                emptyOrFull(jobSearchParams.jobModes(), WorkingMode.values()),
                emptyOrFull(jobSearchParams.requiredAvailabilities(), JobRequiredAvailability.values()),
                jobSearchParams.requiredSkills(),
                Optional.ofNullable(jobSearchParams.requiredSkills())
                        .orElseGet(Set::of)
                        .isEmpty(),
                jobSearchParams.owner(),
                jobSearchParams.startAt(),
                jobSearchParams.finishAt(),
                keywords,
                keywords.isEmpty(),
                loggedUserId,
                pageable
        );
    }

    default <T> Set<T> emptyOrFull(Set<T> input, T[] values) {
        return Optional.ofNullable(input)
                .filter(ts -> !ts.isEmpty())
                .orElseGet(() -> Set.of(values));
    }

    Long countByOwnerId(UUID id);

    @Query("""
                select j from Job j
                    left join fetch j.jobMedia jm
                        inner join fetch jm.media
                    left join fetch j.keywords
                    left join fetch j.owner owner
                    left join fetch j.requiredSkills
                    left join fetch j.tags
                where j.id in (:ids)
            """)
    List<JobSearchInfo> listJobSearchFrom(List<UUID> ids, Sort sort);
}