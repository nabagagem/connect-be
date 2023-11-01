package com.nabagagem.connectbe.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProfileType {
    USER(0), ADMIN(0), PARTNER(-1);

    private final Integer priority;
}
