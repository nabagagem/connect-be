package com.nabagagem.connectbe.controllers.profile;

import com.nabagagem.connectbe.entities.NotificationSettings;
import com.nabagagem.connectbe.services.profile.ProfileNotificationService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/profile/{id}/notification-settings")
public class ProfileNotificationController {
    private final ProfileNotificationService profileNotificationService;

    @PutMapping
    public void update(@PathVariable UUID id,
                       @RequestBody @Valid NotificationSettings notificationSettings) {
        profileNotificationService.update(id, notificationSettings);
    }

    @GetMapping
    public ResponseEntity<NotificationSettings> get(@PathVariable UUID id) {
        return profileNotificationService.getSettings(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
