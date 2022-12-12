package com.nabagagem.connectbe.resources;

import com.nabagagem.connectbe.entities.Address;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.transaction.annotation.Transactional;

@RepositoryRestResource
public interface AddressResource extends PagingAndSortingRepository<Address, String> {
    @Transactional
    @Modifying
    @Query("delete from Address a")
    void delete();

    Address save(Address address);
}
