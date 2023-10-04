package com.nabagagem.connectbe.domain.job;

import com.nabagagem.connectbe.domain.profile.WorkingMode;

public record ProfileJobSearchParams(
        JobStatus jobStatus,
        JobCategory jobCategory,
        WorkingMode jobMode,
        String expression
) {
}
