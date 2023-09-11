package com.nabagagem.connectbe.repos;

import com.nabagagem.connectbe.entities.UserMailNotification;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface UserMailNotificationRepository extends CrudRepository<UserMailNotification, UUID> {
    void deleteByProfileId(UUID profile);
}