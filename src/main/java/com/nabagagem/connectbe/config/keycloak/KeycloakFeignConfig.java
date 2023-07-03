package com.nabagagem.connectbe.config.keycloak;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import feign.RequestInterceptor;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Slf4j
@Configuration
@EnableFeignClients
@AllArgsConstructor
public class KeycloakFeignConfig {
    private final RestTemplate keycloakRestTemplate;
    private final KeycloakProperties keycloakProperties;

    @Bean
    public RequestInterceptor requestInterceptor() {
        return template -> template.header("Authorization", "Bearer " +
                getAdminToken());
    }

    private String getAdminToken() {
        return Optional.ofNullable(
                        getTokenResponse(getLoginRequest(keycloakProperties.getUsername(),
                                keycloakProperties.getPassword())))
                .map(HttpEntity::getBody)
                .map(jsonNodes -> jsonNodes.get("access_token"))
                .map(JsonNode::asText)
                .orElse(null);
    }

    private HttpEntity<MultiValueMap<String, String>> getLoginRequest(String username,
                                                                      String password) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("username", username);
        map.add("password", password);
        map.add("grant_type", "password");
        map.add("client_id", keycloakProperties.getClientName());

        return new HttpEntity<>(map, headers);
    }

    @SuppressWarnings({"rawtypes"})
    @SneakyThrows
    private ResponseEntity<ObjectNode> getTokenResponse(HttpEntity body) {
        try {
            ResponseEntity<ObjectNode> loginEntity = keycloakRestTemplate.postForEntity("/auth/realms/{realm}/protocol/openid-connect/token",
                    body, ObjectNode.class, keycloakProperties.getRealm());

            if (loginEntity.getStatusCode().is2xxSuccessful()) {
                log.info("User logged in successfully: {}", loginEntity.getStatusCode());
                return loginEntity;
            }
        } catch (HttpStatusCodeException e) {
            log.info("Failed to login on keycloak: {} - {}", e.getStatusCode(),
                    e.getResponseBodyAsString());
            throw e;
        }
        return null;
    }
}
