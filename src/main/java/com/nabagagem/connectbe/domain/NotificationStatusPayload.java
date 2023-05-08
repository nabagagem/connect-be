package com.nabagagem.connectbe.domain;

import jakarta.validation.constraints.NotNull;

public record NotificationStatusPayload(
        @NotNull Boolean read
) {
}
