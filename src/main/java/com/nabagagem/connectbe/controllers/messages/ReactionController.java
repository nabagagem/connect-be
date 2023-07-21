package com.nabagagem.connectbe.controllers.messages;

import com.nabagagem.connectbe.domain.ResourceRef;
import com.nabagagem.connectbe.domain.messages.ReactionPayload;
import com.nabagagem.connectbe.services.messages.MessageAuthService;
import com.nabagagem.connectbe.services.messages.MessageReactionService;
import com.nabagagem.connectbe.services.messages.ReactAuthService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@AllArgsConstructor
public class ReactionController {
    private final MessageAuthService messageAuthService;
    private final MessageReactionService messageReactionService;
    private final ReactAuthService reactAuthService;

    @PostMapping("/api/v1/messages/{messageId}/reactions")
    public ResourceRef create(@PathVariable UUID messageId,
                              @RequestBody @Valid ReactionPayload reactionPayload) {
        messageAuthService.failIfUnableToReact(messageId);
        return new ResourceRef(messageReactionService.create(messageId, reactionPayload).getId());
    }

    @DeleteMapping("/api/v1/reactions/{reactionId}")
    public void delete(@PathVariable UUID reactionId) {
        reactAuthService.failIfUnableToDelete(reactionId);
        messageReactionService.delete(reactionId);
    }

}
