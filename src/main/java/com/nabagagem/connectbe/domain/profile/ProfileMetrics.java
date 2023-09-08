package com.nabagagem.connectbe.domain.profile;

import java.time.ZonedDateTime;

public record ProfileMetrics(
        Long publishedJobs,
        Long finishedJobs,
        Long hiredJobs,
        Long workedHours,
        Long recommendationsAmount,
        Long votes,
        ZonedDateTime lastActivity,
        ZonedDateTime firstLogin) {
}
