package com.nabagagem.connectbe.domain.profile;

import com.nabagagem.connectbe.entities.ReportAction;
import com.nabagagem.connectbe.entities.ReportStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@Valid
public record PatchProfilePayload(
        @NotNull ReportStatus reportStatus,
        @NotNull ReportAction reportAction
) {
}
