package com.nabagagem.connectbe.services.bid;

import com.nabagagem.connectbe.domain.bid.BidMessageCommand;
import com.nabagagem.connectbe.domain.messages.SendMessageCommand;
import com.nabagagem.connectbe.domain.messages.SendMessagePayload;
import com.nabagagem.connectbe.entities.Thread;
import com.nabagagem.connectbe.repos.ThreadRepo;
import com.nabagagem.connectbe.services.messages.MessageService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class BidMessageService {
    private final ThreadRepo threadRepo;
    private final MessageService messageService;

    public Optional<Thread> findThreadFor(UUID bidId, UUID userId) {
        return threadRepo.findBy(bidId, userId);
    }

    public void addMessage(BidMessageCommand bidMessageCommand) {
        messageService.send(new SendMessageCommand(
                bidMessageCommand.loggedUserId(),
                new SendMessagePayload(null,
                        bidMessageCommand.bidId(),
                        bidMessageCommand.textPayload().text())
        ));
    }
}
