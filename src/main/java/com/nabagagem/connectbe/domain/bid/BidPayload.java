package com.nabagagem.connectbe.domain.bid;

import com.nabagagem.connectbe.entities.MoneyAmount;

import java.util.UUID;

public record BidPayload(
        UUID id,
        MoneyAmount budget,
        Integer amountOfHours,
        String experience,
        String highlights,
        String targetJobTitle,
        String targetJobId
) {
}
