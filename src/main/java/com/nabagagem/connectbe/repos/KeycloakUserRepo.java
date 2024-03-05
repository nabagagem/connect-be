package com.nabagagem.connectbe.repos;

import com.nabagagem.connectbe.entities.ConnectProfile;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface KeycloakUserRepo extends CrudRepository<ConnectProfile, UUID> {

    @Query(value =
            """
                        select id from user_entity where email = :email
                    """, nativeQuery = true)
    Optional<String> getLegacyUserId(String email);

}
