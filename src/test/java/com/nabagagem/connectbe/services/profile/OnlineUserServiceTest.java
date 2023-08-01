package com.nabagagem.connectbe.services.profile;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.nabagagem.connectbe.config.keycloak.KeycloakAdminClient;
import com.nabagagem.connectbe.config.keycloak.KeycloakProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OnlineUserServiceTest {

    @Mock
    private KeycloakProperties mockKeycloakProperties;
    @Mock
    private KeycloakAdminClient mockKeycloakAdminClient;

    private OnlineUserService onlineUserServiceUnderTest;

    @BeforeEach
    void setUp() {
        onlineUserServiceUnderTest = new OnlineUserService(mockKeycloakProperties, mockKeycloakAdminClient);
    }

    @Test
    void testIsOnline() {
        // Setup
        when(mockKeycloakProperties.getRealm()).thenReturn("realm");

        // Configure KeycloakAdminClient.getSessionsFrom(...).
        final ArrayNode jsonNodes = new ArrayNode(new JsonNodeFactory(false));
        when(mockKeycloakAdminClient.getSessionsFrom("realm", "b50e4d95-cf68-40c5-9cfe-e5bdd8fed677"))
                .thenReturn(jsonNodes);

        // Run the test
        final boolean result = onlineUserServiceUnderTest.isOnline(
                UUID.fromString("b50e4d95-cf68-40c5-9cfe-e5bdd8fed677"));

        // Verify the results
        assertThat(result).isFalse();
    }

    @Test
    void testIsOnline_KeycloakAdminClientReturnsNull() {
        // Setup
        when(mockKeycloakProperties.getRealm()).thenReturn("realm");
        when(mockKeycloakAdminClient.getSessionsFrom("realm", "b50e4d95-cf68-40c5-9cfe-e5bdd8fed677")).thenReturn(null);

        // Run the test
        final boolean result = onlineUserServiceUnderTest.isOnline(
                UUID.fromString("b50e4d95-cf68-40c5-9cfe-e5bdd8fed677"));

        // Verify the results
        assertThat(result).isFalse();
    }
}
