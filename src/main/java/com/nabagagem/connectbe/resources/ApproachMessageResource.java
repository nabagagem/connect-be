package com.nabagagem.connectbe.resources;

import com.nabagagem.connectbe.entities.ApproachMessage;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.UUID;

@RepositoryRestResource
public interface ApproachMessageResource extends PagingAndSortingRepository<ApproachMessage, UUID>,
        CrudRepository<ApproachMessage, UUID> {
}
