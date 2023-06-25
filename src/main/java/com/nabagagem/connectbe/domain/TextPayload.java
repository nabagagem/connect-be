package com.nabagagem.connectbe.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TextPayload(
        @NotBlank @Size(max = 1000) String text
) {
}
