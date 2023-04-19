package com.nabagagem.connectbe.domain;

import java.time.DayOfWeek;
import java.util.Map;
import java.util.Set;

public record AvailabilityCommand(
        String id,
        Map<DayOfWeek, Set<AvailabilityType>> availabilities
) {
}
