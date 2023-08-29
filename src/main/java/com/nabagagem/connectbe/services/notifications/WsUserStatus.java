package com.nabagagem.connectbe.services.notifications;

import com.nabagagem.connectbe.domain.messages.ChatStatus;

public record WsUserStatus(ChatStatus status,
                           String threadId) {
}

