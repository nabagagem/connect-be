package com.nabagagem.connectbe.resources;

import com.nabagagem.connectbe.entities.Account;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.transaction.annotation.Transactional;

@RepositoryRestResource
public interface AccountResource extends PagingAndSortingRepository<Account, String> {
    @Transactional
    @Modifying
    @Query("delete from Account a")
    void delete();

    Account save(Account account);
}
