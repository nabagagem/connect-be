package com.nabagagem.connectbe.repos;

import com.nabagagem.connectbe.entities.ProfileSkill;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Set;
import java.util.UUID;

@JaversSpringDataAuditable
public interface ProfileSkillRepo extends CrudRepository<ProfileSkill, UUID> {

    @Query("""
                select ps from ProfileSkill ps
                    inner join fetch ps.skill
                where ps.profile.id = :id
            """)
    Set<ProfileSkill> findByProfileId(UUID id);

    @Modifying
    @Query("delete from ProfileSkill ps where ps.profile.id = :profileId")
    void deleteByProfileId(UUID profileId);
}