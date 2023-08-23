package com.nabagagem.connectbe.repos;

import com.nabagagem.connectbe.entities.Skill;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@JaversSpringDataAuditable
@Repository
public interface SkillRepo extends CrudRepository<Skill, UUID> {
    Optional<Skill> findByName(String name);
}