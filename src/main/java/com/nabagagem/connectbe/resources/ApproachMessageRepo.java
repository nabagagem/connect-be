package com.nabagagem.connectbe.resources;

import com.nabagagem.connectbe.entities.ApproachMessage;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ApproachMessageRepo extends
        CrudRepository<ApproachMessage, UUID> {
}
