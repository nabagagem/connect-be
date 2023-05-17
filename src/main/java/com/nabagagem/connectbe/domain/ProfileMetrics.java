package com.nabagagem.connectbe.domain;

import java.time.LocalDateTime;

public record ProfileMetrics(
        Long finishedJobs,
        Long hiredJobs,
        Long workedHours,
        Long recommendationsAmount,
        Long votes,
        LocalDateTime lastActivity,
        LocalDateTime firstLogin) {
}
