package com.nabagagem.connectbe.domain.bid;

import com.nabagagem.connectbe.domain.messages.TextPayload;

import java.util.UUID;

public record BidMessageCommand(UUID bidId, UUID loggedUserId, TextPayload textPayload) {
}
