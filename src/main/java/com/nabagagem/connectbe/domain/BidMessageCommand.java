package com.nabagagem.connectbe.domain;

import java.util.UUID;

public record BidMessageCommand(UUID bidId, UUID loggedUserId, TextPayload textPayload) {
}
