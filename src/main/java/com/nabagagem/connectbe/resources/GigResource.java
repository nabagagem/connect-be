package com.nabagagem.connectbe.resources;

import com.nabagagem.connectbe.entities.Gig;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@RepositoryRestResource
public interface GigResource extends PagingAndSortingRepository<Gig, UUID> {
    @Transactional
    @Modifying
    @Query("delete from Gig g")
    int delete();

    Gig save(Gig gig);
}