package com.nabagagem.connectbe.controllers;

import com.nabagagem.connectbe.domain.NotificationItemPayload;
import com.nabagagem.connectbe.domain.NotificationStatusPayload;
import com.nabagagem.connectbe.domain.UpdateNotifCommand;
import com.nabagagem.connectbe.services.notifications.NotificationMapper;
import com.nabagagem.connectbe.services.notifications.NotificationService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
