package com.nabagagem.connectbe.domain;

import com.nabagagem.connectbe.entities.MoneyAmount;

import java.util.UUID;

public record BidPayload(
        UUID id,
        MoneyAmount budget,
        String timeEstimation,
        String experience,
        String highlights,
        String targetJobTitle,
        String targetJobId
) {
}
