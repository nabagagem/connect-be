package com.nabagagem.connectbe.domain.notification;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UpdateNotifCommand(@NotNull UUID notificationId,
                                 @Valid @NotNull NotificationStatusPayload statusPayload) {
}
