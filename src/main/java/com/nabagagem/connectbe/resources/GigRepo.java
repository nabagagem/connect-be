package com.nabagagem.connectbe.resources;

import com.nabagagem.connectbe.entities.Gig;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.UUID;

@SuppressWarnings({"SpringDataRepositoryMethodParametersInspection", "unused"})
@Repository
public interface GigRepo extends CrudRepository<Gig, UUID> {
    Set<Gig> findByTagsIn(Set<String> tags);
}