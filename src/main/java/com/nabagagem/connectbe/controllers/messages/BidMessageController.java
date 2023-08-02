package com.nabagagem.connectbe.controllers.messages;

import com.nabagagem.connectbe.domain.bid.BidMessageCommand;
import com.nabagagem.connectbe.domain.messages.TextPayload;
import com.nabagagem.connectbe.domain.messages.ThreadMessage;
import com.nabagagem.connectbe.entities.Thread;
import com.nabagagem.connectbe.services.bid.BidMessageService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
public class BidMessageController implements MessageMediaUrlTrait {
    private final BidMessageService bidMessageService;

    @GetMapping("/api/v1/bids/{bidId}/thread")
    public BidThread getMessages(@PathVariable UUID bidId,
                                 Principal principal) {
        return bidMessageService.findThreadFor(bidId, UUID.fromString(principal.getName()))
                .map(this::formatMessages)
                .orElse(null);
    }

    @PostMapping("/api/v1/bids/{bidId}/messages")
    public void create(@RequestBody @Valid TextPayload textPayload,
                       @PathVariable UUID bidId,
                       Principal principal) {
        bidMessageService.addMessage(new BidMessageCommand(bidId,
                UUID.fromString(principal.getName()),
                textPayload));
    }

    private BidThread formatMessages(Thread thread) {
        return new BidThread(
                thread.getSender().getId(),
                thread.getSender().getPersonalInfo().getPublicName(),
                thread.getRecipient().getId(),
                thread.getRecipient().getPersonalInfo().getPublicName(),
                thread.getMessages().stream().map(message -> new ThreadMessage(
                        message.getId(),
                        message.getThread().getId(),
                        message.getText(),
                        message.getAudit().getCreatedBy(),
                        getUrlFrom(message),
                        null, null,
                        message.getAudit().getModifiedAt(),
                        message.getRead(),
                        Set.of(),
                        message.getTextUpdated()
                )).collect(Collectors.toList())
        );
    }

    private record BidThread(
            UUID senderId,
            String senderName,
            UUID recipientId,
            String recipientName,
            List<ThreadMessage> messages
    ) {
    }
}
