package com.nabagagem.connectbe.domain;

import java.time.LocalDateTime;

public record ProfileMetrics(
        Integer finishedJobs,
        Integer hiredJobs,
        Integer workedHours,
        Integer recommendationsAmount,
        Integer votes,
        LocalDateTime lastLogin,
        LocalDateTime firstLogin
) {
}
