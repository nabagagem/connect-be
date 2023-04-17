package com.nabagagem.connectbe.resources;

import com.nabagagem.connectbe.entities.Certification;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Set;
import java.util.UUID;

public interface CertificationRepo extends CrudRepository<Certification, UUID> {
    @Modifying
    @Query("delete from Certification c where c.profile.id = :profileId")
    void deleteByProfileId(UUID profileId);

    Set<Certification> findByProfileId(UUID id);
}