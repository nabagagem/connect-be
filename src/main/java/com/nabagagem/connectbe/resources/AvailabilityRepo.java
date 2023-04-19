package com.nabagagem.connectbe.resources;

import com.nabagagem.connectbe.entities.Availability;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Set;
import java.util.UUID;

public interface AvailabilityRepo extends CrudRepository<Availability, UUID> {
    @Modifying
    @Query("delete from Availability a where a.profile.id = :id")
    void deleteByProfileId(UUID id);

    Set<Availability> findByProfileId(UUID id);
}