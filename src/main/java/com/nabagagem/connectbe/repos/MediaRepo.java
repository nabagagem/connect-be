package com.nabagagem.connectbe.repos;

import com.nabagagem.connectbe.entities.Media;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MediaRepo extends CrudRepository<Media, UUID> {
}