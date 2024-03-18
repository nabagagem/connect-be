package com.nabagagem.connectbe.repos;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Repository
@AllArgsConstructor
public class UserInfoRepo {
    private final RestTemplate cognitoRestTemplate;

    public CognitoUserInfo getCurrentUserInfo() {
        return cognitoRestTemplate.getForObject("/oauth2/userinfo", CognitoUserInfo.class);
    }

    public record CognitoUserInfo(
            UUID sub,
            String email,
            String username
    ) {
    }
}
