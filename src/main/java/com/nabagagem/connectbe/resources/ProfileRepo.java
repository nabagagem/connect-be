package com.nabagagem.connectbe.resources;

import com.nabagagem.connectbe.entities.ConnectProfile;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProfileRepo extends
        CrudRepository<ConnectProfile, UUID> {
}
