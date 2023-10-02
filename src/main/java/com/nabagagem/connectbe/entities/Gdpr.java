package com.nabagagem.connectbe.entities;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class Gdpr {
    @Enumerated(EnumType.STRING)
    private GdprLevel gdprLevel;
}