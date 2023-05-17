package com.nabagagem.connectbe.repos;

import com.nabagagem.connectbe.entities.ProfileReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProfileReportRepository extends CrudRepository<ProfileReport, UUID> {
    @Query(value = """
            select r from ProfileReport r
                left join fetch r.reporter
                left join fetch r.targetJob
                left join fetch r.targetProfile
            """, countQuery = """
                select count(r) from ProfileReport r
            """)
    Page<ProfileReport> findPage(Pageable pageable);

    @Query("""
            select r from ProfileReport r
                left join fetch r.reporter
                left join fetch r.targetJob
                left join fetch r.targetProfile
            where r.id = :id
            """)
    Optional<ProfileReport> findFullBy(UUID id);
}