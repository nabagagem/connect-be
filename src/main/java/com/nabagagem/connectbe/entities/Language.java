package com.nabagagem.connectbe.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Locale;

@AllArgsConstructor
@Getter
public enum Language {
    ENGLISH(Locale.ENGLISH), PORTUGUESE(Locale.forLanguageTag("pt-BR"));

    private final Locale locale;
}
