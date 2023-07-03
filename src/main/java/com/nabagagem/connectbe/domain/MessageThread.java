package com.nabagagem.connectbe.domain;

import com.nabagagem.connectbe.entities.ThreadStatus;

import java.time.ZonedDateTime;

public record MessageThread(
        String id,
        String recipientId,
        ThreadStatus status,
        String recipientName,
        String senderId,
        String senderName,
        ZonedDateTime lastMessageAt,
        String lastMessageText,
        String lastModifiedBy
) {
}
