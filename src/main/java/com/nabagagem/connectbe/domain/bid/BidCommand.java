package com.nabagagem.connectbe.domain.bid;

import com.nabagagem.connectbe.entities.MoneyAmount;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record BidCommand(
        @NotNull MoneyAmount budget,
        @NotNull UUID owner,
        @Positive Integer amountOfHours,
        @Size(max = 1000) String experience,
        @Size(max = 1000) String highlights,
        @NotNull UUID jobId
) {
}
