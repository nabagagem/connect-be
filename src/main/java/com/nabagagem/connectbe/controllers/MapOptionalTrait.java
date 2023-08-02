package com.nabagagem.connectbe.controllers;

import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public interface MapOptionalTrait {
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    default <T> ResponseEntity<T> map(@NotNull Optional<T> optional) {
        return optional.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
