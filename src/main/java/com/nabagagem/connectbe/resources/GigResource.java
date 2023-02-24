package com.nabagagem.connectbe.resources;

import com.nabagagem.connectbe.entities.Gig;
import com.nabagagem.connectbe.entities.GigType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Set;
import java.util.UUID;

@SuppressWarnings("SpringDataRepositoryMethodParametersInspection")
@RepositoryRestResource
public interface GigResource extends CrudRepository<Gig, UUID> {
    Set<Gig> findByTagsIn(Set<String> tags);

    Set<Gig> findByTagsInAndGigType(Set<String> tags, GigType gigType);
}