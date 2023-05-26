package com.nabagagem.connectbe.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

@Valid
public record RatingPayload(@NotNull UUID targetProfileId,
                            @NotNull @Min(1) @Max(5) Integer stars,
                            @Size(min = 15, max = 1000) String description) {

}
