package com.nabagagem.connectbe.repos;

import com.nabagagem.connectbe.entities.Media;
import com.nabagagem.connectbe.entities.ReportPic;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@JaversSpringDataAuditable
public interface ReportPicRepository extends CrudRepository<ReportPic, UUID> {
    List<ReportPic> findByProfileReportId(UUID reportId);

    @Query("""
                select p.media from ReportPic p
                where p.id = :id
            """)
    Optional<Media> findMediaByPicId(UUID id);
}