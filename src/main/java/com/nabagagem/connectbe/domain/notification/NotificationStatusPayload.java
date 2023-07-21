package com.nabagagem.connectbe.domain.notification;

import jakarta.validation.constraints.NotNull;

public record NotificationStatusPayload(
        @NotNull Boolean read
) {
}
