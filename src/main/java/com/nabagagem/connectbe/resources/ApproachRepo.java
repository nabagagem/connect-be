package com.nabagagem.connectbe.resources;

import com.nabagagem.connectbe.entities.Approach;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ApproachRepo extends
        CrudRepository<Approach, UUID> {
}
