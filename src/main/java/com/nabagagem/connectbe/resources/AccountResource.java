package com.nabagagem.connectbe.resources;

import com.nabagagem.connectbe.entities.Account;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;
import java.util.UUID;

@RepositoryRestResource
@Validated
public interface AccountResource extends
        CrudRepository<Account, UUID> {

    @SuppressWarnings("unused")
    Optional<Account> findByUserId(String userId);

}
