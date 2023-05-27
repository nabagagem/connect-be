package com.nabagagem.connectbe.domain;

import java.time.ZonedDateTime;

public record MessageThread(
        String id,
        String recipientId,
        String recipientName,
        String senderId,
        String senderName,
        ZonedDateTime lastMessageAt,
        String lastMessageText
) {
}
