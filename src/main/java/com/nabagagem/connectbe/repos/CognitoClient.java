package com.nabagagem.connectbe.repos;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

import static com.nabagagem.connectbe.repos.UserInfoRepo.CognitoUserInfo;

@Slf4j
@Repository
@AllArgsConstructor
public class CognitoClient {
    private final RestTemplate cognitoRestTemplate;

    @SuppressWarnings("unused")
    @Cacheable(cacheNames = "cognito-userinfo")
    public CognitoUserInfo getCurrentUserInfo(String sub) {
        try {
            return cognitoRestTemplate.getForObject("/oauth2/userinfo", CognitoUserInfo.class);
        } catch (RestClientException e) {
            log.warn("Failed to get user info from cognito: {}", e.getMessage());
            if (e.getMessage().contains("invalid_token")) {
                return new CognitoUserInfo(UUID.fromString(sub), null, null);
            }
        }
        return null;
    }
}
