package com.nabagagem.connectbe.entities;

import java.time.ZonedDateTime;
import java.util.UUID;

public interface ProfileThreadItem {
    UUID getId();

    UUID getRecipientId();

    Boolean getRecipientPublicProfile();

    ThreadStatus getStatus();

    String getRecipientName();

    UUID getSenderId();

    String getSenderName();

    ZonedDateTime getLastMessageAt();

    String getLastMessageText();

    String getLastModifiedBy();

    Integer getUnreadCount();

    MessageType getLastMessageType();
}
