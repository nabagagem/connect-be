package com.nabagagem.connectbe.domain.profile;

import java.time.DayOfWeek;
import java.util.Map;

public record AvailabilityCommand(
        java.util.UUID id,
        Map<DayOfWeek, AvailabilityType> availabilities
) {
}
