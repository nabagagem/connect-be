package com.nabagagem.connectbe.repos;

import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Repository
@AllArgsConstructor
public class UserInfoRepo {
    private final CognitoClient cognitoClient;

    public CognitoUserInfo getCurrentUserInfo() {
        return cognitoClient.getCurrentUserInfo(
                SecurityContextHolder.getContext()
                        .getAuthentication().getName()
        );
    }

    public record CognitoUserInfo(
            UUID sub,
            String email,
            String username
    ) {
    }
}
