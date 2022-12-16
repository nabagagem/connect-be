package com.nabagagem.connectbe.resources;

import com.nabagagem.connectbe.entities.Gig;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.UUID;

@RepositoryRestResource
public interface GigResource extends PagingAndSortingRepository<Gig, UUID>, CrudRepository<Gig, UUID> {
    
}