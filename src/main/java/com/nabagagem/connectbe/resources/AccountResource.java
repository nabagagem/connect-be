package com.nabagagem.connectbe.resources;

import com.nabagagem.connectbe.entities.Account;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;
import java.util.UUID;

@RepositoryRestResource
public interface AccountResource extends PagingAndSortingRepository<Account, UUID>,
        CrudRepository<Account, UUID> {

    @SuppressWarnings("unused")
    Optional<Account> findByUserId(String userId);

}
