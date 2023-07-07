package com.nabagagem.connectbe.repos;

import com.nabagagem.connectbe.entities.Reaction;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.util.UUID;

public interface ReactionRepository extends CrudRepository<Reaction, UUID> {
    @Query("select (count(r) > 0) from Reaction r where r.id = :id and r.audit.createdBy = :createdBy")
    boolean existsByIdAndOwner(@Param("id") @NonNull UUID id, @Param("createdBy") @NonNull String createdBy);
}