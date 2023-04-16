package com.nabagagem.connectbe.resources;

import com.nabagagem.connectbe.entities.GigArea;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface GigAreaRepo extends CrudRepository<GigArea, UUID> {
}
