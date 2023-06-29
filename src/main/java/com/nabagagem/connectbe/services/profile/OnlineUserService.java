package com.nabagagem.connectbe.services.profile;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.nabagagem.connectbe.config.keycloak.KeycloakAdminClient;
import com.nabagagem.connectbe.config.keycloak.KeycloakProperties;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class OnlineUserService {
    private final KeycloakProperties keycloakProperties;
    private final KeycloakAdminClient keycloakAdminClient;

    public boolean isOnline(UUID profileId) {
        ArrayNode sessions = keycloakAdminClient.getSessionsFrom(
                keycloakProperties.getRealm(),
                profileId.toString()
        );
        return Optional.ofNullable(sessions)
                .map(__ -> !sessions.isEmpty())
                .orElse(false);
    }
}
