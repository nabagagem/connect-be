package com.nabagagem.connectbe.domain.rating;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Valid
public record CreateRatingCommand(
        @NotNull @Valid RatingPayload ratingPayload,
        @NotNull UUID sourceUserId
) {
}
