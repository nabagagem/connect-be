package com.nabagagem.connectbe.domain;

import com.nabagagem.connectbe.entities.EventMode;
import com.nabagagem.connectbe.entities.EventType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

@Valid
public record EventSearchParams(
        List<EventMode> eventMode,
        List<EventType> eventType,
        @NotNull LocalDate from,
        @NotNull LocalDate to
) {
}
