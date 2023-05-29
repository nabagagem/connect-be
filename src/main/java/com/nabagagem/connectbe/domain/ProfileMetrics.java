package com.nabagagem.connectbe.domain;

import java.time.ZonedDateTime;

public record ProfileMetrics(
        Long finishedJobs,
        Long hiredJobs,
        Long workedHours,
        Long recommendationsAmount,
        Long votes,
        ZonedDateTime lastActivity,
        ZonedDateTime firstLogin) {
}
