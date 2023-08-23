package com.nabagagem.connectbe.repos;

import com.nabagagem.connectbe.entities.ProfileMedia;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Set;
import java.util.UUID;

@JaversSpringDataAuditable
public interface ProfileMediaRepository extends CrudRepository<ProfileMedia, UUID> {

    @Query("""
                select pm from ProfileMedia pm
                    inner join fetch pm.media
                where pm.profile.id = :profileId
            """)
    Set<ProfileMedia> getProfileMedia(UUID profileId);
}