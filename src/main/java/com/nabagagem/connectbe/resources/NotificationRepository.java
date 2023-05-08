package com.nabagagem.connectbe.resources;

import com.nabagagem.connectbe.entities.Notification;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends CrudRepository<Notification, UUID> {
    List<Notification> findByTargetProfileId(UUID userId);

    @Modifying
    @Query("update Notification set read = :read where id = :id")
    void update(UUID id, Boolean read);
}