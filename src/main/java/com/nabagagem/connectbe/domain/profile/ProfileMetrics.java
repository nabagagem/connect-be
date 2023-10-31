package com.nabagagem.connectbe.domain.profile;

import java.time.ZonedDateTime;

public record ProfileMetrics(
        ZonedDateTime lastActivity,
        ZonedDateTime firstLogin) {
}
