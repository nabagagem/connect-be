package com.nabagagem.connectbe.domain.report;

import com.nabagagem.connectbe.entities.ReportAction;
import com.nabagagem.connectbe.entities.ReportStatus;
import com.nabagagem.connectbe.entities.ReportType;

import java.util.UUID;

public record ReportItemPayload(
        UUID id,
        ReportType reportType,
        UUID targetProfileId,
        String targetProfileName,
        UUID targetJobId,
        String targetJobTitle,
        UUID reporterProfileId,
        String reporterProfileName,
        String description,
        ReportStatus reportStatus,
        ReportAction reportAction
) {
}
