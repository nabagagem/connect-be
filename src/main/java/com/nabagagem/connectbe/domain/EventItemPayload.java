package com.nabagagem.connectbe.domain;

import com.nabagagem.connectbe.entities.EventMode;
import com.nabagagem.connectbe.entities.EventType;
import com.nabagagem.connectbe.entities.MoneyAmount;

import java.time.LocalDate;
import java.util.UUID;

public record EventItemPayload(
        UUID id,
        String title,
        LocalDate eventDate,
        EventMode eventMode,
        EventType eventType,
        String address,
        MoneyAmount price,
        String externalLink,
        String description
) {
}
