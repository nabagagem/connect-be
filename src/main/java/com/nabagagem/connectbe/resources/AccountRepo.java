package com.nabagagem.connectbe.resources;

import com.nabagagem.connectbe.entities.Account;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AccountRepo extends
        CrudRepository<Account, UUID> {
}
