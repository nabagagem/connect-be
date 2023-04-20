package com.nabagagem.connectbe.clients;

import lombok.Value;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@FeignClient(name = "keycloak", dismiss404 = true,
        url = "${keycloak.url}",
        configuration = KeycloakFeignConfig.class)
public interface KeycloakAdminClient {

    @PutMapping(path = "/admin/realms/{realm}/users/{userId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<String> updateUserAttributes(@PathVariable String realm,
                                                @PathVariable String userId,
                                                @RequestBody Map<String, Object> userInfo);

    @GetMapping(path = "/admin/realms/{realm}/users/{userId}/sessions",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    List<KeycloakUserInfo> getSessionsFrom(@PathVariable String realm,
                                           @PathVariable String userId);

    @DeleteMapping("/admin/realms/{realm}/sessions/{id}")
    void deleteSession(@PathVariable String realm,
                       @PathVariable String id);

    @GetMapping(path = "/admin/realms/{realm}/users/{userId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    Optional<KeycloakUserInfo> getUserInfo(@PathVariable String realm,
                                           @PathVariable String userId);

    @DeleteMapping(path = "/admin/realms/{realm}/users/{userId}")
    void deleteUser(@PathVariable String realm,
                    @PathVariable String userId);

    @PutMapping(path = "/admin/realms/{realm}/users/{userId}/reset-password",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    void updatePassword(@PathVariable String realm,
                        @PathVariable String userId,
                        @RequestBody KeycloakPasswordPayload keycloakPasswordPayload);

    @Value
    class KeycloakPasswordPayload {
        String type = "password";
        String value;
        String temporary = "false";
    }

    @Value
    class KeycloakUserInfo {
        String username;
        Boolean enabled;
        String firstName;
        String lastName;
        Map<String, List<String>> attributes;
    }
}
