package com.nabagagem.connectbe.repos;

import com.nabagagem.connectbe.domain.FilePurpose;
import com.nabagagem.connectbe.domain.messages.JobMediaInfo;
import com.nabagagem.connectbe.entities.JobMedia;
import com.nabagagem.connectbe.entities.Media;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface JobMediaRepository extends CrudRepository<JobMedia, UUID> {

    @Query("""
                select jm from JobMedia jm
                    inner join fetch jm.media
                where jm.job.id = :jobId
            """)
    Set<JobMediaInfo> listForJobId(UUID jobId);

    @Query("""
                select m from JobMedia jm
                    inner join jm.media m
                where jm.job.id = :jobId
                and jm.filePurpose = :filePurpose
                and jm.position = :position
            """)
    Optional<Media> findMediaFrom(UUID jobId, FilePurpose filePurpose, Integer position);

    @Query("""
                select jm from JobMedia jm
                    inner join fetch jm.media
                where jm.job.id = :jobId
                and jm.filePurpose = :filePurpose
                and jm.position = :position
            """)
    Optional<JobMedia> findFrom(UUID jobId, FilePurpose filePurpose, Integer position);

    @Query("""
                select jm from JobMedia jm
                    inner join fetch jm.media
                where jm.job.id = :id
            """)
    Set<JobMedia> findFromJob(UUID id);
}