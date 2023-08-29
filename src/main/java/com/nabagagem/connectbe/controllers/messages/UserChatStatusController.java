package com.nabagagem.connectbe.controllers.messages;

import com.nabagagem.connectbe.domain.messages.UserChatStatusCommand;
import com.nabagagem.connectbe.services.messages.UserChatStatusService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@AllArgsConstructor
public class UserChatStatusController {
    private final UserChatStatusService userChatStatusService;

    @PostMapping("/api/v1/threads/{threadId}/user-status")
    public void update(@PathVariable UUID threadId,
                       @RequestBody @Valid UserChatStatusCommand userChatStatusCommand) {
        userChatStatusService.update(threadId, userChatStatusCommand);
    }

}
