package com.nabagagem.connectbe.domain;

public record ProfileJobSearchParams(
        JobStatus jobStatus,
        JobCategory jobCategory,
        JobMode jobMode,
        String expression
) {
}
