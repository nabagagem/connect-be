package com.nabagagem.connectbe.domain;

import jakarta.validation.constraints.NotBlank;

public record TextPayload(
        @NotBlank String text
) {
}
