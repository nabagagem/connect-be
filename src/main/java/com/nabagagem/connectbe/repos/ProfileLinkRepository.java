package com.nabagagem.connectbe.repos;

import com.nabagagem.connectbe.entities.ConnectProfile;
import com.nabagagem.connectbe.entities.ProfileLink;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface ProfileLinkRepository extends CrudRepository<ProfileLink, UUID> {
    void deleteByProfile(ConnectProfile profile);
}