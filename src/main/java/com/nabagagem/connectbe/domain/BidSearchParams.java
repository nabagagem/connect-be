package com.nabagagem.connectbe.domain;

import com.nabagagem.connectbe.entities.BidStatus;
import jakarta.validation.constraints.NotNull;

public record BidSearchParams(
        @NotNull BidDirection direction,
        BidStatus bidStatus
) {
}
