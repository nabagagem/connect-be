package com.nabagagem.connectbe.services;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.AbstractOAuth2TokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.security.Principal;
import java.util.Optional;
import java.util.UUID;

public interface LoggedUserIdTrait {
    default Optional<UUID> loggedUser() {
        return Optional.ofNullable(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .map(Principal::getName)
                .filter(name -> !name.contains("anonymous"))
                .map(UUID::fromString);
    }

    default UUID getLoggedUserId() {
        return loggedUser().orElseThrow();
    }

    default Optional<Jwt> getCurrentToken() {
        return Optional.ofNullable(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .filter(authentication -> authentication instanceof JwtAuthenticationToken)
                .map(authentication -> (JwtAuthenticationToken) authentication)
                .map(AbstractOAuth2TokenAuthenticationToken::getToken);
    }
}
