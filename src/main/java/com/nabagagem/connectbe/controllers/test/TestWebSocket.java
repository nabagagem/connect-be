package com.nabagagem.connectbe.controllers.test;

import com.nabagagem.connectbe.domain.TextPayload;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@ConditionalOnProperty("ramifica.web-socket.enabled")
public class TestWebSocket {
    private final SimpMessagingTemplate simpMessagingTemplate;

    @PostMapping("/api/v1/test-ws/{id}")
    public void send(@PathVariable String id,
                     @RequestBody @Valid TextPayload textPayload) {
        simpMessagingTemplate.convertAndSend(
                "/topics/user/" + id,
                textPayload);
    }

}
