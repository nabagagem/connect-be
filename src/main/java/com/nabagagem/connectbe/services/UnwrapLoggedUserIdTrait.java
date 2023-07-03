package com.nabagagem.connectbe.services;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.security.Principal;
import java.util.Optional;
import java.util.UUID;

public interface UnwrapLoggedUserIdTrait {
    default Optional<UUID> unwrapLoggedUserId() {
        return Optional.ofNullable(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .map(Principal::getName)
                .filter(name -> !name.contains("anonymous"))
                .map(UUID::fromString);
    }

    default UUID getUserIdOrFail() {
        return unwrapLoggedUserId().orElseThrow();
    }
}
