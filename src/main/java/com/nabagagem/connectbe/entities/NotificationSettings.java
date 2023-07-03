package com.nabagagem.connectbe.entities;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class NotificationSettings {
    private Boolean enableMessageEmail;
}