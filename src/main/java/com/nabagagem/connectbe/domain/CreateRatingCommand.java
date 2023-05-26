package com.nabagagem.connectbe.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Valid
public record CreateRatingCommand(
        @NotNull RatingPayload ratingPayload,
        @NotNull UUID sourceUserId
) {
}
