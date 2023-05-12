package com.nabagagem.connectbe.resources;

import com.nabagagem.connectbe.entities.Media;
import com.nabagagem.connectbe.entities.ReportPic;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReportPicRepository extends CrudRepository<ReportPic, UUID> {
    List<ReportPic> findByProfileReportId(UUID reportId);

    @Query("""
                select p.media from ReportPic p
                where p.id = :id
            """)
    Optional<Media> findMediaByPicId(UUID id);
}