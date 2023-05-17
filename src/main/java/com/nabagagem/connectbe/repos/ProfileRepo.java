package com.nabagagem.connectbe.repos;

import com.nabagagem.connectbe.entities.ConnectProfile;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProfileRepo extends
        CrudRepository<ConnectProfile, UUID> {
    @Query("""
                select p.id from ConnectProfile p
                where p.personalInfo.slug = :slug
            """)
    Optional<UUID> findIdFromSlug(String slug);
}
