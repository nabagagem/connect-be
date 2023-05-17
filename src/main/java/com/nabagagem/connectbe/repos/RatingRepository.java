package com.nabagagem.connectbe.repos;

import com.nabagagem.connectbe.entities.Rating;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface RatingRepository extends CrudRepository<Rating, UUID> {
    Long countByTargetProfileId(UUID id);
}