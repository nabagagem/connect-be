package com.nabagagem.connectbe.resources;

import com.nabagagem.connectbe.entities.Address;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AddressRepo extends CrudRepository<Address, UUID> {
}
