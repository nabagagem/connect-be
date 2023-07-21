package com.nabagagem.connectbe.controllers.notification;

import com.nabagagem.connectbe.domain.notification.NotificationItemPayload;
import com.nabagagem.connectbe.domain.notification.NotificationStatusPayload;
import com.nabagagem.connectbe.domain.notification.UpdateNotifCommand;
import com.nabagagem.connectbe.services.notifications.NotificationMapper;
import com.nabagagem.connectbe.services.notifications.NotificationService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;
    private final NotificationMapper notificationMapper;

    @GetMapping("/api/v1/profile/{id}/notifications")
    public List<NotificationItemPayload> list(@PathVariable UUID id) {
        return notificationService.list(id).stream()
                .map(notificationMapper::toDto)
                .collect(Collectors.toList());
    }

    @PutMapping("/api/v1/notifications/{notificationId}")
    public void update(@RequestBody @Valid NotificationStatusPayload statusPayload,
                       @PathVariable UUID notificationId) {
        notificationService.update(new UpdateNotifCommand(notificationId, statusPayload));
    }
}
