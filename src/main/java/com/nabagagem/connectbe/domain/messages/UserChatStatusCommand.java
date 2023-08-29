package com.nabagagem.connectbe.domain.messages;

import java.util.UUID;

public record UserChatStatusCommand(
        UUID sourceUserId,
        ChatStatus status
) {
}
