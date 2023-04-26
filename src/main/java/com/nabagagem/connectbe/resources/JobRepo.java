package com.nabagagem.connectbe.resources;

import com.nabagagem.connectbe.entities.Job;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface JobRepo extends CrudRepository<Job, UUID> {
}