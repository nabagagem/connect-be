package com.nabagagem.connectbe.services.profile;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.nabagagem.connectbe.repos.CognitoUserInfoRepo;
import com.nabagagem.connectbe.repos.KeycloakUserRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.nabagagem.connectbe.repos.CognitoUserInfoRepo.CognitoUserInfo;

@Service
@AllArgsConstructor
public class UserInfoService {
    private final CognitoUserInfoRepo cognitoUserInfoRepo;
    private final KeycloakUserRepo keycloakUserRepo;

    public UserInfo getCurrentUserInfo() {
        CognitoUserInfo currentUserInfo = cognitoUserInfoRepo.getCurrentUserInfo();
        return new UserInfo(
                currentUserInfo,
                keycloakUserRepo.getLegacyUserId(currentUserInfo.email())
                        .map(UUID::fromString)
                        .orElse(currentUserInfo.sub())
        );
    }

    public record UserInfo(
            @JsonUnwrapped CognitoUserInfo cognitoUserInfo,
            UUID userId
    ) {

    }

}
