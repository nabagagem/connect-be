package com.nabagagem.connectbe.entities;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class NotificationSettings {
    private Boolean enableMessagePushNotification;
    private Boolean enableMessageMail;
}
