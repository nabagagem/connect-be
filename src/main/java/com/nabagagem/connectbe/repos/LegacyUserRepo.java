package com.nabagagem.connectbe.repos;

import com.nabagagem.connectbe.entities.ConnectProfile;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LegacyUserRepo extends CrudRepository<ConnectProfile, UUID> {

    @Cacheable(cacheNames = "legacy-user")
    @Query(value =
            """
                        select id from user_entity where email = :email
                    """, nativeQuery = true)
    Optional<String> getLegacyUserId(String email);

}
