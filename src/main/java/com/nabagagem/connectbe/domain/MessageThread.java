package com.nabagagem.connectbe.domain;

import java.time.LocalDateTime;

public record MessageThread(
        String id,
        String recipientId,
        String recipientName,
        String senderId,
        String senderName,
        LocalDateTime lastMessageAt,
        String lastMessageText
) {
}
