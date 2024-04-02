package com.nabagagem.connectbe.repos;

import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import static com.nabagagem.connectbe.repos.UserInfoRepo.*;

@Repository
@AllArgsConstructor
public class CognitoClient {
    private final RestTemplate cognitoRestTemplate;

    @SuppressWarnings("unused")
    @Cacheable(cacheNames = "cognito-userinfo")
    public CognitoUserInfo getCurrentUserInfo(String sub) {
        return cognitoRestTemplate.getForObject("/oauth2/userinfo", CognitoUserInfo.class);
    }
}
