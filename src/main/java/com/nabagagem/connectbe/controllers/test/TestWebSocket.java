package com.nabagagem.connectbe.controllers.test;

import com.nabagagem.connectbe.domain.NotificationCommand;
import com.nabagagem.connectbe.domain.TextPayload;
import com.nabagagem.connectbe.entities.NotificationType;
import com.nabagagem.connectbe.resources.ProfileRepo;
import com.nabagagem.connectbe.services.notifications.NotificationService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@AllArgsConstructor
public class TestWebSocket {
    private final NotificationService notificationService;
    private final ProfileRepo profileRepo;

    @PostMapping("/api/v1/test-ws/{id}")
    public void send(@PathVariable UUID id,
                     @RequestBody @Valid TextPayload textPayload) {
        notificationService.create(
                new NotificationCommand(
                        profileRepo.findById(id).orElseThrow(),
                        textPayload.text(),
                        "test", NotificationType.TEST
                )
        );
    }

}