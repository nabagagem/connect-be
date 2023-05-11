package com.nabagagem.connectbe.domain;

import com.nabagagem.connectbe.entities.EventMode;
import com.nabagagem.connectbe.entities.EventType;
import com.nabagagem.connectbe.entities.MoneyAmount;
import jakarta.validation.Valid;

import java.time.LocalDate;

@Valid
public record EventPayload(
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
