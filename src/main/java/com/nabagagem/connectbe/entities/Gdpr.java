package com.nabagagem.connectbe.entities;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class Gdpr {
    @ElementCollection
    @Enumerated(EnumType.STRING)
    private Set<GdprLevel> gdprLevels;
}