package com.nabagagem.connectbe.domain.profile;

import com.nabagagem.connectbe.entities.ReportType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Valid
public record CreateProfileReportPayload(
        ReportType reportType,
        UUID targetProfileId,
        UUID targetJobId,
        @NotNull UUID reporterProfileId,
        String description
) {
}
