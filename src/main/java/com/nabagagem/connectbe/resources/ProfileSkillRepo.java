package com.nabagagem.connectbe.resources;

import com.nabagagem.connectbe.entities.ProfileSkill;
import org.springframework.data.repository.CrudRepository;

import java.util.Set;
import java.util.UUID;

public interface ProfileSkillRepo extends CrudRepository<ProfileSkill, UUID> {
    Set<ProfileSkill> findByProfileId(UUID uuid);
}