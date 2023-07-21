package com.nabagagem.connectbe.domain.job;

import com.nabagagem.connectbe.domain.job.JobCategory;
import com.nabagagem.connectbe.domain.job.JobMode;
import com.nabagagem.connectbe.domain.job.JobStatus;

public record ProfileJobSearchParams(
        JobStatus jobStatus,
        JobCategory jobCategory,
        JobMode jobMode,
        String expression
) {
}
